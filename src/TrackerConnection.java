import java.awt.SecondaryLoop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for all communication with tracker node
 * @author Hmama Adem
 *
 */
public class TrackerConnection extends Connection{

	public TrackerConnection(String ip, int port) {
		super(ip, port);
	}


	public  List<SimpleEntry<String , Integer>>  getfile(String key) throws Exception{

		String request = "getfile " + key; 
		makeRequest(request);
		List<SimpleEntry<String, Integer>> ret = new ArrayList<>();
	
		acceptNext("peers");
		String returnedKey = readUntil(' ');
		if(!returnedKey.equals(key)){
			// TODO : handel error : throw exception
			System.out.println("error parsing response from trakcer : invalid key returned in response ");
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
