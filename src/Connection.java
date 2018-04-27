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
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws Exception
	 */
	protected void makeRequest(String request) throws UnknownHostException, IOException {
        soc = new Socket(ip,port);
        is = new BufferedInputStream(soc.getInputStream());
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);	
		System.out.println("about to send the request "+ request);
		writer.println(request);
		writer.flush();
	}
	
	/**
	 * Free ressources
	 * @throws IOException 
	 * @throws Exception
	 */
	protected void endRequest() throws IOException {
		is.close();
		writer.close();
		soc.close();
	}

	
	protected  void accept(String word) throws ProtocolException, IOException{
		String found = "";
		for(int i=0;i<word.length();i++){
			int ret = is.read();
			found +=(char ) ret	;
			if(ret == -1 || ((char) ret)!=word.charAt(i)) 
				throw new ProtocolException("Expecting token <" + word+">, found: <" + found+">");
		}
	}
	
	
	protected void acceptNext(String word) throws ProtocolException, IOException{
		escapeWhite();
		accept(word);
		escapeWhite();
	}
	
	protected  void escapeWhite() throws IOException {
		while(true){
			is.mark(2); 
			char c = (char ) is.read();
			if(c == ' ') continue;
		    is.reset();	
		    break;
		}
	}
	
	
	protected String readUntil(char c) throws IOException {
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
	
	protected String readUntil(char a,char b) throws IOException {
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
	
	protected char peekNext() throws IOException {
		// TODO: if i'm peeking, read should not return -1, throw exception in case!
		is.mark(2);
		char c = (char) is.read();
		is.reset();
		return c;
	}
	
}
