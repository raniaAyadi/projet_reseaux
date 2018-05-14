package peer.client;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import peer.ApplicationContext;
import peer.Config;
import peer.server.ProtocolException;

/**
 * Abstract class providing basic tcp connection utilities like  establishing connection, sending request and other methods 
 * useful for parsing responses
 * @author Hmama Adem
 *
 */
public abstract class Connection{
	protected String ip;
	protected int port;
	protected BufferedInputStream is;
	protected PrintWriter writer;
	protected Socket soc;	
	
	public Connection(String ip,int port){
		this.ip = ip;
		this.port = port;
	}
	
	public String getIp() {
		return ip;
	}


	public int getPort() {
		return port;
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
		if(port !=Config.trackerPort)
			Config.downloadLog.config("The message : "+request+" is sent to "+ip+":"+port);
		System.out.println(request +" send to "+port);
      
		soc = new Socket(ip,port);
        is = new BufferedInputStream(soc.getInputStream());
		writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);	
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
