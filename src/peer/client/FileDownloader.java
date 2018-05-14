package peer.client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import peer.ApplicationContext;
import peer.Config;
import peer.storage.FileTracker;
import peer.storage.Piece;

import java.util.HashSet;

/**
 * This class implements a thread, one thread is started for any leeching file
 * FileDownloader uses all peer connections announced by the file tracker
 * 
 * @author Adem Hmama
 * @version 1.0
 * 
 */
public class FileDownloader implements Runnable {

	private FileTracker ft;
	private Set<PeerConnection> connections;
	private Swarm swarm;
	private ExecutorService executor;
	private int maxPiece;

	public FileDownloader(FileTracker ft) throws Exception {
		this.ft = ft;
		this.maxPiece = (int) (Config.messageMaxSize / this.ft.getPieceSize());

		connections = new HashSet<>();
		List<SimpleEntry<String, Integer>> ret = ApplicationContext.trackerConnection.getfile(ft.getKey());
		for (int i = 0; i < ret.size(); i++) {
			SimpleEntry<String, Integer> ent = ret.get(i);
			PeerConnection con = new PeerConnection(ent.getKey(), ent.getValue().intValue(), ft);
			connections.add(con);
		}

		executor = Executors.newFixedThreadPool(Config.peerConnectionNumber);
		Timer t = new Timer();
		this.swarm = new Swarm(ft, connections, t);
		t.scheduleAtFixedRate(swarm, 0, Config.updatePeriod);
	}

	private boolean downloadPieces(List<Piece> p) {
		Map<PeerConnection, List<Integer>> m = new HashMap<>();
		if (p != null) {
			for (Piece i : p) {
				PeerConnection pc = selectPeer(i.getSeeder());
				if (m.containsKey(pc) == false)
					m.put(pc, new ArrayList<>());
				m.get(pc).add(i.getIndex());
			}

			for (PeerConnection pc : m.keySet()) {
				PieceDownloader th = new PieceDownloader(ft, pc, m.get(pc));
				executor.execute(th);
			}
			return true;
		}

		else
			return false;
	}

	private boolean downlaodRandomPieces() {
		List<Piece> p = swarm.selectRamdomPiece(this.maxPiece);
		return downloadPieces(p);
	}

	private boolean downloadRarestPieces() {
		List<Piece> p = swarm.selectRarestPiece(this.maxPiece);
		return downloadPieces(p);
	}

	private PeerConnection selectPeer(List<PeerConnection> s) {
		int i = (int) (Math.random() * s.size());
		int j = 0;
		PeerConnection pc = null;
		for (PeerConnection p : s) {
			pc = p;
			if (j == i)
				break;
			j++;
		}
		return pc;
	}

	/**
	 * Download algo
	 */
	@Override
	public void run() {
		Config.downloadLog.info("start-download: " + ft.getFileName());
		downlaodRandomPieces();

		while (!ft.isSeeding()) {
			// wait if user paused download
			if (ft.isSuspended()) {
				Config.downloadLog.info("download-paused: " + ft.getFileName());
				synchronized (ft.suspendLock) {
					try {
						ft.suspendLock.wait();
						Config.downloadLog.info("download-resumed: " + ft.getFileName());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			// wait in order to respect max down speed constraint
			if (!ft.downloadAllowed()) {
				synchronized (ft.statLock) {
					try {
						ft.statLock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			if (ft.isTerminated())
				break;
			downloadRarestPieces();
		}
		Config.downloadLog.info("download-complete: " + ft.getFileName());
	}

}
