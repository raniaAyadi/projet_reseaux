import java.util.BitSet;
import java.util.List;


public class PeerConnection {

	  private String peerIp;
	  private int peerPort;
	  private BitSet bufferMap; // the bitmap of the file in the 
	
	  
	  public ResponseObject sendRequest(String file_key,List<Integer> pieceOffsets){
		    // initiate request
		    // parse response and assemble it in response object
		    // close connection
		  	return null;
		  }
	  
	  
	  public void run() throws InterruptedException{
		  while(true){
			  // update bufferMap
			  Thread.sleep(Constant.Config.UPDATE_BUFFERMAP_TIMER);
		  }
	  }
}
