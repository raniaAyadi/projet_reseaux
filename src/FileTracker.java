import java.util.BitSet;
import java.util.List;


public class FileTracker {

	
	
	  public void updateBufferMap(){
		  
	  }
	
	  private String key;
	  private Long pieceSize;
	  private Long size;
	  
	  
	  BitSet bitmap;

	  private List<PeerConnection> peers; // reference to all peers that are sharing this file
	  // this will  be null if the file is totally downloaded
	
}
