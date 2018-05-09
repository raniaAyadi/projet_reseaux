import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PieceDownloader implements Runnable {	
	
	private PeerConnection con;
	private List<Integer> req;
	private FileTracker ft;
	
	public PieceDownloader(FileTracker fileTrakcer, PeerConnection c, List<Integer> pieces) {
		try {
			this.con = new PeerConnection(c, ft);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.req = pieces;
		this.ft = fileTrakcer;
	}
	
	public PieceDownloader(FileTracker ft, PeerConnection c, Integer i) {
		try {
			this.con = new PeerConnection(c, ft);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		this.ft = ft;
		this.req = new ArrayList<>();
		req.add(i);
	}
	
	@Override
	public void run() {
		System.out.println("PieceDownload : "+req);
		try {
			Map<Integer, byte[]> ret = this.con.getpieces(req, ft);
			for(Integer i : ret.keySet()) {
				byte[] tosave =  ret.get(i);
				if(tosave == null)
					continue;
				
			    ft.addPiece(tosave, i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
