
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashSet;

/**
 * This class implements a thread, one thread is started for any leeching file
 * FileDownloader uses all peer connections announced by the file tracker 
 * @author Adem Hmama
 * @version 1.0
 * 
 */
public class FileDownloader implements Runnable {

	private FileTracker ft;
	private Set<PeerConnection> connections;
	private Swarm swarm;
	private ExecutorService executor;
	
	FileDownloader(FileTracker ft) throws Exception{
		this.ft = ft;
		connections = new HashSet<>();
		List<SimpleEntry<String , Integer>> ret =  ApplicationContext.trackerConnection.getfile(ft.getKey());
		for(int i=0;i<ret.size();i++){
			SimpleEntry<String , Integer> ent = ret.get(i);
			PeerConnection con = new PeerConnection(ent.getKey(), ent.getValue().intValue(), ft);
			connections.add(con);
		}
		
		executor = Executors.newFixedThreadPool(Config.peerConnectionNumber);
		Timer t = new Timer();
		this.swarm = new Swarm(ft, connections, t);
		t.scheduleAtFixedRate(swarm, 0, Config.updatePeriod);
	}
	
	private boolean downloadRarestPiece(){
		Piece p = swarm.selectRarestPiece();
		if(p != null) {
			PeerConnection pc = selectPeer(p.getSeeder());
			if(pc == null)
				return false;
			
			PieceDownloader th = new PieceDownloader(ft, pc, p.getIndex());
			executor.execute(th);
			return true;
		}
		else
			return false;
	}
	
	private PeerConnection selectPeer(List<PeerConnection> s) {
		int i = (int)(Math.random()*s.size());
		int j =0;
		PeerConnection pc = null;
		
		for(PeerConnection p : s) {
			pc = p;
			if(j == i)
				break;
			j++;
		}
		
		return pc;
	}
	
	/**
	 * Just a first basic version of the thread
	 */
	@Override
	public void run() {
		while(!ft.isSeeding()) {
			// wait if user paused download
						if (ft.isSuspended()) {
							synchronized (ft.suspendLock) {
								try {
									ft.suspendLock.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						
						// wait in order to respect max down speed constraint
						if(!ft.downloadAllowed()){
							synchronized (ft.statLock) {
								try {
									ft.statLock.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
						
						if(ft.isTerminated())
			break;
						
			downloadRarestPiece();
		}
	}
	
}
