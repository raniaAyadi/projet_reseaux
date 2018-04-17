import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

/**
 * *
 * @author Adem Hmama
 * @version 1.0
 * 
 * the role of this class is to use all possible ressources (connected peers) to download the hole file
 *
 */
public class FileDownloader implements Runnable {

	
	private FileTracker ft;
	private List<PeerConnection> connections;
	
	FileDownloader(FileTracker ft) throws Exception{
		this.ft = ft;
		connections = new ArrayList<>();
		List<SimpleEntry<String , Integer>> ret =  Peer.trackerConnection.getfile(ft.getKey());
		for(int i=0;i<ret.size();i++){
			SimpleEntry<String , Integer> ent = ret.get(i);
			PeerConnection con = new PeerConnection(ent.getKey(), ent.getValue().intValue(), ft);
			connections.add(con);
		}
	}
	
	private int getRandom(){
		// TODO: make it random 
		return ft.getNumberPieces()/2;
	}
	
	@Override
	public void run() {
		// locate last index reached (if reached)
		boolean found = false;
		int i = 0;
		int startIndex = 0;
		while(i!=ft.getNumberPieces()){
			if(found && !ft.getBufferMap().get(i)){
				startIndex = i;
				break;
			}
			if(ft.getBufferMap().get(i)){
				found = true;
			}
			i++;
		}
		if(!found)
			startIndex = getRandom();
		
		// loop throw the remaining pieces, and download  each piece from one of the connections
		int connIndex = -1;
		int nb_conns = connections.size();
		// TODO: treat case where there are no connections
		while(!ft.isSeeding()){
			// select next connection to use
			connIndex = (connIndex+1) %nb_conns;
			PeerConnection con = connections.get(connIndex);
			List<Integer> req = new ArrayList<>();
			req.add(new Integer(startIndex));
			try {
				Map<Integer, byte[]> res =  con.getpieces(req,ft);
				byte[] tosave =  res.get(startIndex);
				ft.addPiece(tosave, startIndex);
				startIndex = (startIndex + 1)%ft.getNumberPieces();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		      // for each connected peer make a piece choice and:
		      // ResponseObject obj = PeerConnection.sendRequest(file,pieceOffset...);
		    /* 
			try{
		    	  System.out.println("file downloader thread");
		    	  Thread.sleep(100);
		    	  // operation.addPiece....
		    	  // FileTracker.updateBufferMap();
		      }catch(Exception e){
		        // ...
		      }*/
		}
		
	}
	
}
