package peer.client;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peer.Config;
import peer.server.ProtocolException;
import peer.storage.FileTracker;

/**
 * This class acts as an interface between the running application and other peers on the network,
 * protocols are implemented in this class
 * @author Hmama Adem
 *
 */
public class PeerConnection extends Connection {
	private String bufferMap; // the bitmap of the file in the "connected" peer	
	
	public PeerConnection(String ip, int port, FileTracker ft) throws Exception {
		super(ip, port);
		updateBufferMap(ft);
	}
	
	//Initier une nouvelle connexion 
	public PeerConnection(PeerConnection p, FileTracker ft){
		super(p.getIp(), p.getPort());
	}
	
	// debug
	public  void printbuffer(){
		System.out.println(bufferMap);
	}

	/**
	 * getpieces
	 * @param offsets
	 * @param ft
	 * @return
	 * @throws Exception
	 */
	public Map<Integer, byte[]> getpieces( List<Integer> offsets,FileTracker ft) throws Exception {
		Map<Integer, byte[]> ret = new HashMap<>();
		
		String req = "getpieces " + ft.getKey() + " [";	
		for (int i = 0; i < offsets.size(); i++) {
			req += offsets.get(i);
			if (i != offsets.size() - 1)
				req += " ";
		}
		req += "]";
		
		makeRequest(req);
		acceptNext("data");
		String returnedKey = readUntil(' ', '[');
		if(!ft.getKey().equals(returnedKey)){
			Config.downloadLog.warning("implementation error: returned key is different from what i asked for");
		}
		acceptNext("[");
		while(peekNext() != ']'){
			escapeWhite();
			Integer index = new Integer(readUntil(':'));
			accept(":");
			byte[] data = new byte[ft.getPieceSize()];
			ret.put(index, data);
		}
		
		endRequest();
		return ret;
	}
	
	/**
	 * Not yet tested!
	 * send your buffer map and get the other peer's buffer map in response
	 * @param myBufferMap
	 * @return
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws ProtocolException 
	 */
	public void have(String myBufferMap,String key) throws UnknownHostException, IOException, ProtocolException{
		String req = "have " + key + " " + myBufferMap;
		makeRequest(req);
		acceptNext("have");
		readUntil(' ');
		escapeWhite();
		String ret = readUntil(' ');
		escapeWhite();
		endRequest();
		
		setBufferMap(ret);
	}

	/**
	 * Interested
	 * @param ft
	 * @throws Exception
	 */
	private void updateBufferMap(FileTracker ft) throws Exception {
		String req = "interested " + ft.getKey();
		makeRequest(req);
		
		acceptNext("have");
		String returnedKey = readUntil(' ');
		if(!ft.getKey().equals(returnedKey)){
			Config.downloadLog.warning("implementation error: wrong key returned from the other peer");
		}
		
		escapeWhite();
		String strBuf = readUntil(' ');
		if(strBuf.length() !=  ft.getBuffermap().length()){
			Config.downloadLog.warning("error parsing the buffer map of response returned by another peer");
		}
		
		setBufferMap(strBuf);
		endRequest();
	}
	
	private void setBufferMap(String strBuf) {
		bufferMap = strBuf;
	}
	
	public String getBufferMap() {
		return bufferMap;
	}
	
	public boolean equals(Object o) {
		if(o instanceof PeerConnection == false)
			return false;
		
		if(o == this)
			return true;
		
		PeerConnection p = (PeerConnection) o;
		if(p.ip.equals(this.ip) == true && p.port == this.port)
			return true;
		else 
			return false;
	}
}
