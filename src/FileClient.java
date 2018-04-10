import java.util.List;


public class FileClient implements Runnable {

	
	private FileTracker fileTracker;
	private List<PeerConnection> connections;
	
	FileClient(FileTracker ft){
		fileTracker = ft;
	}
	
	
	@Override
	public void run() {
		while(true){
		      // for each connected peer make a piece choice and:
		      // ResponseObject obj = PeerConnection.sendRequest(file,pieceOffset...);
		      try{
		    	  // operation.addPiece....
		    	  // FileTracker.updateBufferMap();
		      }catch(Exception e){
		        // ...
		      }
		}
		
	}
	
}
