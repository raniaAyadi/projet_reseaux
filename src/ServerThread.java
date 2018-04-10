import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe ServerThread
 * ServerThread hérite la classe Thread
 * un serverThread est associé à une connexion particulière identifiée par un socket passé en paramètre
 */

public class ServerThread extends Thread {
	private Socket socket ;     
	private Integer numClient;    
	private Logger log;           
	private Config config;
	PrintWriter out;
	BufferedReader in;
	/**
	 * 
	 * @param socket docket du client
	 * @param numClient le numéro du client
	 * @throws IOException
	 */
	public ServerThread(Socket socket, Integer numClient) throws IOException{
		this.socket = socket;
		this.numClient = numClient;
		this.config = Config.getInstance();
		
    	this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream());

		this.log = Logger.getLogger(ServerThread.class.getName());
		this.log.log(Level.INFO,this+ "is connected");
	}
	
	private InitRequestServer receiveInit() throws IOException, ProtocolException {
		log.log(Level.INFO," receive initialization request");
		
		String message = Operation.readInputStream(in);
		log.log(Level.INFO, "Message received is : " + message);
		
		return InitRequestServer.getInstance(message);
	}
	
	private void sendInit(InitRequestServer req) throws IOException, ProtocolException {
		InitResponseServer res = new InitResponseServer(req);
		String message = res.getMessage();
		
		log.log(Level.INFO, "Message to send is :"+message);
		out.println(message);
		out.flush();
	}
	
	private GetRequestServer receiveGet() throws IOException, ProtocolException{
		log.log(Level.INFO," receive get request");
		
		String message = Operation.readInputStream(in);
		log.log(Level.INFO, "Message received is : " + message);
		
		return GetRequestServer.getInstance(message);
	}
	
	private void sendGet(GetRequestServer req) throws ProtocolException, IOException {
		GetResponseServer res = new GetResponseServer(req);
		String message = res.getMessage();
		
		log.log(Level.INFO, "Message to send is : "+message);
		out.println(message);
		out.flush();
	}
	
	public void run() {
		log.log(Level.INFO, Thread.currentThread().getName());
		
		InitRequestServer req = null;
		
		while(req == null) {
			try {
				req =  this.receiveInit();
			} catch (IOException e) {
			    e.printStackTrace();
			    this.out.println("Erreur interne");
			    out.flush();
			    disconnect();
			} catch (ProtocolException e) {
				e.printStackTrace();
				this.out.println(e.getMessage());
				this.out.flush();
				req = null;
			}
		}
		
		try {
			this.sendInit(req);
		} catch (IOException e) {
			e.printStackTrace();
		    this.out.println("Erreur interne");
		    out.flush();
		    disconnect();
		    return;
		} catch (ProtocolException e) {
			e.printStackTrace();
			this.out.println(e.getMessage());
			this.out.flush();
			this.disconnect();
			return ;
		}
		
		GetRequestServer req2 = null;
		while(req2 == null) {
			try {
				req2 =  this.receiveGet();
			} catch (IOException e) {
			    e.printStackTrace();
			    this.out.println("Erreur interne");
			    out.flush();
			    disconnect();
			} catch (ProtocolException e) {
				e.printStackTrace();
				this.out.println(e.getMessage());
				this.out.flush();
				req2 = null;
			}
		}
		
		try {			this.sendGet(req2);
		} catch (IOException e) {
			e.printStackTrace();
		    this.out.println("Erreur interne");
		    out.flush();
		} catch (ProtocolException e) {
			e.printStackTrace();
			this.out.println(e.getMessage());
			this.out.flush();
		}
		finally {
			this.disconnect();
		}
	}
	
	private void disconnect(){
		this.out.println("Session is end");
		this.out.flush();
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.close();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.log(Level.INFO, this + " has disconnected");
	}
	
	public String toString(){
		return "The client "+this.numClient+" who has address : "+this.socket.getInetAddress();
	}
}