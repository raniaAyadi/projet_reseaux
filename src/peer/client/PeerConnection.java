package peer.client;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import peer.server.ProtocolException;
import peer.storage.FileTracker;

/**
 * This class acts as an interface between the running application and other peers on the network,
 * protocols are implemented in this class
 * @author Hmama Adem
 *
 */
public class PeerConnection extends Connection {
	private String bufferMap; // the bitmap of the file in the	
	
	public PeerConnection(String ip, int port, FileTracker ft) throws Exception {
		super(ip, port);
		updateBufferMap(ft);
	}
	
	public PeerConnection(PeerConnection p, FileTracker ft) throws Exception {
		super(p.getIp(), p.getPort());
	}
	
	// just for debug
	public  void printbuffer(){
		System.out.println(bufferMap);
	}

	// TODO: handel io/protocole exception at this level !! also all the other methods and the trackerconnection methods
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
			// TODO: throw exception , handel error 
			System.out.println("error getpieces: returned key is deffeent from what i asked for !! your lucky i didnt throw an exception");
		}
		acceptNext("[");
		while(peekNext() != ']'){
			escapeWhite();
			Integer index = new Integer(readUntil(':'));
			accept(":");
			//System.out.println("ft.getpiecesize: " + ft.getPieceSize());
			byte[] data = new byte[ft.getPieceSize()];
			//System.out.println("data size: " + data.length);
			int nb = is.read(data);
			if(nb != ft.getPieceSize()){
				// TODO: error from the other peer
				//System.out.println("error when reading data at index: " + index);
				//System.out.println("read " + nb +" bytes instead of " + ft.getPieceSize() + " (piece size)");
			}
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

	// interested
	private void updateBufferMap(FileTracker ft) throws Exception {
		String req = "interested " + ft.getKey();
		makeRequest(req);
		
		acceptNext("have");
		String returnedKey = readUntil(' ');
		if(!ft.getKey().equals(returnedKey)){
			// TODO: throw exception 
			System.out.println("wrong key returned from the other peer");
		}
		
		escapeWhite();
		String strBuf = readUntil(' ');
		if(strBuf.length() !=  ft.getBuffermap().length()){
			// TODO : error throw exception
			System.out.println("error parsing the buffer map of response returned by another peer");
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
