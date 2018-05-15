package peer.client;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import peer.Constant;
import peer.storage.FileTracker;

/**
 * Classe PieceDownloader
 * PieceDonwlaoder etablie la demande de telechargement d'un ensemble de pieces avec un seul Peer
 */

public class PieceDownloader implements Runnable {	
	
	private PeerConnection con;
	private List<Integer> req;
	private FileTracker ft;
	private Logger log;
	
	public PieceDownloader(FileTracker fileTrakcer, PeerConnection c, List<Integer> pieces) {	
		this.con = new PeerConnection(c, ft);
		this.log =Logger.getLogger(Constant.Log.DOWNLOAD_LOG);
		this.req = pieces;
		this.ft = fileTrakcer;
	}
	
	@Override
	public void run() {
		try {
			Map<Integer, byte[]> ret = this.con.getpieces(req, ft);
			for(Integer i : ret.keySet()) {
				byte[] tosave =  ret.get(i);
				if(tosave == null)
					continue;
				
			    ft.addPiece(tosave, i); 
			    log.fine("got piece <"+i+"> from <"+con.ip+">");
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.warning("failed to download pieces");
		}
	}
}
