import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Abstract class providing basic tcp connection utilities like  establishing connection, sending request and other methods 
 * useful for parsing responses
 * @author Hmama Adem
 *
 */
public abstract class Connection {
	protected String ip;
	protected int port;
	protected BufferedInputStream is;
	protected PrintWriter writer;
	protected Socket soc;
	
	
	public Connection(String ip,int port){
		this.ip = ip;
		this.port = port;
	}
	
	/**
	 * Establishes connection with server, sends a string formatted request, and sets up the inputStream
	 * which will be parsed by the caller method depending on the protocol's expected response
	 * @param request
	 * @throws Exception
	 */
	protected void makeRequest(String request) throws Exception{
        soc = new Socket(ip,port);
        is = new BufferedInputStream(soc.getInputStream());
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);	
		writer.println(request);
		writer.flush();
	}
	
	/**
	 * Free ressources
	 * @throws Exception
	 */
	protected void endRequest() throws Exception{
		is.close();
		writer.close();
		soc.close();
	}

	
	protected  boolean accept(String word) throws Exception{
		for(int i=0;i<word.length();i++){
			int ret = is.read();
			if(ret == -1 || ((char) ret)!=word.charAt(i)) return false;
		}
		return true;
	}
	
	
	protected boolean acceptNext(String word) throws Exception{
		escapeWhite();
		if(!accept(word)) return false;
		escapeWhite();
		return true;
	}
	
	protected  void escapeWhite() throws Exception{
		while(true){
			is.mark(2); 
			char c = (char ) is.read();
			if(c == ' ') continue;
		    is.reset();	
		    break;
		}
	}
	
	
	protected String readUntil(char c) throws Exception{
		String res = "";
		while(true){
			is.mark(2);
			int ret = is.read();
			if(ret == -1) break;
			char k = (char ) ret;
			if(k == c) {
				is.reset();
				break;
			}
			res+=k;
		}
		return res;
	}
	
	protected String readUntil(char a,char b) throws Exception{
		String res = "";
		while(true){
			is.mark(2);
			char k = (char ) is.read();
			if(k == a || k == b) {
				is.reset();
				break;
			}
			res+=k;
		}
		return res;
	}
	
	protected char peekNext() throws Exception{
		// TODO: if i'm peeking, read should not return -1, throw exception in case!
		is.mark(2);
		char c = (char) is.read();
		is.reset();
		return c;
	}
	
}
