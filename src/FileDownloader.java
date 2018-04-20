import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

/**
 * This class implements a thread, one thread is started for any leeching file
 * FileDownloader uses all peer connections announced by the file tracker 
 * @author Adem Hmama
 * @version 1.0
 * 
 */
public class FileDownloader implements Runnable {

	
	private FileTracker ft;
	private List<PeerConnection> connections;
	private boolean suspend;
	private boolean endThread;
	
	FileDownloader(FileTracker ft) throws Exception{
		suspend = false;
		endThread = false;
		this.ft = ft;
		connections = new ArrayList<>();
		List<SimpleEntry<String , Integer>> ret =  ApplicationContext.trackerConnection.getfile(ft.getKey());
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
	
	// TODO : update this method
	/**
	 * Just a first basic version of the thread
	 */
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
			if(endThread){
				break;
			}
			if(suspend){
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			
			// select next connection to use
			connIndex = (connIndex+1) %nb_conns;
			PeerConnection con = connections.get(connIndex);
			List<Integer> req = new ArrayList<>();
			req.add(new Integer(startIndex));
			try {
				Map<Integer, byte[]> res =  con.getpieces(req,ft);
				byte[] tosave =  res.get(startIndex);
				if(tosave == null)
					continue;
			    ft.addPiece(tosave, startIndex);
				startIndex = (startIndex + 1)%ft.getNumberPieces();
			} catch (Exception e1) {
				continue;
			}
			
	
			// TODO: set up download speed using thread.sleep ? => no use nb_down_max (attribute in filetracker)
		    /* 
			try{
		    	  Thread.sleep(100);
		      }catch(Exception e){
		        // ...
		     }*/
		}
		// automatically removes himself when terminated from list of downloads
		ApplicationContext.fileDownloaders.remove(ft.id);
	}

	public void pause() {
		suspend = true;
	}
	
	public void resume(){
		suspend = false;
	}
	
	public void stop(){
		endThread = true;
	}
	
}
