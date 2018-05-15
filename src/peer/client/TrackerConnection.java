package peer.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.AbstractMap.SimpleEntry;

import peer.ApplicationContext;
import peer.Config;
import peer.server.ProtocolException;
import peer.storage.FileInfo;
import peer.storage.FileTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface for all communication with tracker node
 * @author Hmama Adem
 *
 */
public class TrackerConnection extends Connection{
	
	public TrackerConnection(String ip, int port, boolean ann) throws UnknownHostException, IOException, ProtocolException {
		super(ip, port);
		if(ann == true)
			announce();
	}
	
	//à vérfier si " leech[] " est obsolète !
	public void announce() throws UnknownHostException, IOException, ProtocolException {
		
		String req = "announce listen " + Config.listenPort + " seed [";
		//List<String> leecher_keys = new ArrayList<>();
		boolean first = true;
		for (Map.Entry<String, FileTracker> entry : ApplicationContext.fileTrackers.entrySet()) {
			if(!first) req+=" ";
			else first = false;
			FileTracker ft = entry.getValue();
			if(!ft.hasPart()){
				//leecher_keys.add(ft.getKey())
				continue;
			}
			req+=ft.getFileName() + " "+ ft.getSize() + " " + ft.getPieceSize() + " " + ft.getKey();
		}
		req+="]";
		
//		if(leecher_keys.size() !=0){
//			req+=" leech [";
//			for(int i =0 ;i< leecher_keys.size();i++){
//				req+=leecher_keys.get(i);
//				if(i != leecher_keys.size()-1){
//					req+= " ";
//				}
//			}
//			req+="]";
//		}	
		makeRequest(req);
		
		escapeWhite();
		acceptNext("ok");
		endRequest();
		
	}
	
	/**
	 * Make a look request to tracker, each non null parameter will be added to the list of constraints 
	 * (tested !)
	 * 
	 * @param filename
	 * @param minFileSize
	 * @param maxFileSize
	 * @return
	 * @throws Exception
	 */
	public List<FileInfo> look(String filename, Integer minFileSize,Integer maxFileSize) throws Exception{
		// TODO: parameters validation
		// construct req
		String req = "look [";
		boolean set = false;
		if(filename != null){
			req+="filename=\""+filename+"\"";
			set = true;
		}
		
		if(minFileSize != null){
			if(set) req+= " ";
			set = true;
			req+="filesize>\""+ minFileSize + "\"";
		}
		
		if(maxFileSize != null){
			if(set) req+=" ";
			set = true;
			req+="filesize<\""+ maxFileSize + "\"";
		}
		req+="]";
		
		makeRequest(req);
		
		List<FileInfo> ret = new ArrayList<>();
		
		acceptNext("list");
		acceptNext("[");
		while(peekNext() != ']'){
			String fileName = readUntil(' ');
			escapeWhite();
			String fileSize = readUntil(' ');
			escapeWhite();
			String pieceSize = readUntil(' ');
			escapeWhite();
			String key = readUntil(' ', ']');
			escapeWhite();
			FileInfo info = new FileInfo(fileName, fileSize, pieceSize, key);
			ret.add(info);
		}
		acceptNext("]");
		
		endRequest();
		return ret;
	}


	public  List<SimpleEntry<String , Integer>>  getfile(String key) throws Exception{

		String request = "getfile " + key; 
		makeRequest(request);

		List<SimpleEntry<String, Integer>> ret = new ArrayList<>();
	
		acceptNext("peers");
		String returnedKey = readUntil(' ');
		if(!returnedKey.equals(key)){
			Config.generalLog.warning("error parsing response from trakcer : invalid key returned in response ");
		}
		acceptNext("[");
		while(peekNext() != ']'){
			escapeWhite();
			String ip = readUntil(':');
			accept(":");
			Integer port = new Integer(readUntil(' ',']'));
			ret.add(new SimpleEntry<String, Integer>(ip, port));
			escapeWhite();
		}
		endRequest();
		return ret;
	}
	
}
