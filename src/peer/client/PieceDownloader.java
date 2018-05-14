package peer.client;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import peer.Constant;
import peer.storage.FileTracker;


public class PieceDownloader implements Runnable {	
	
	private PeerConnection con;
	private List<Integer> req;
	private FileTracker ft;
	private Logger log;
	
	public PieceDownloader(FileTracker fileTrakcer, PeerConnection c, List<Integer> pieces) {
		try {
			this.con = new PeerConnection(c, ft);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			    log.fine("The piece "+i+" is sent by "+con.ip);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
