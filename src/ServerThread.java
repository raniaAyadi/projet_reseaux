
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe ServerThread
 * ServerThread hérite la classe Thread
 * un serverThread est associé à une connexion particulière identifiée par un socket passé en paramètre
 */

public class ServerThread implements Runnable {
	private Socket socket ;     
	private Integer numClient;    
	private Logger log;           
	OutputStream out;
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
		
    	this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = this.socket.getOutputStream();

		this.log = Logger.getLogger(ServerThread.class.getName());
		this.log.log(Level.INFO,this+ "is connected");
	}
	
	/**
	 * 
	 * @param className pour instancier la classe concrète en applelant le fabriquant
	 * @return instance de la classe className
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private Request receive() throws IOException, ProtocolException{
		String message = in.readLine();
		log.log(Level.INFO, "Message received is : " + message);
		
		Request req = RequestFactory.createRequest(message);
		if(req == null) {
			//A préciser l'exception (ClassNotFoundEception, etc)
			throw new IOException();
		}
		
		return req;
	}
	
	private void send(Request req) throws IOException, ProtocolException {
		Response res = ResponseFactory.createResponse(req, out);
		res.sendMessage(socket.getInetAddress());
		//log.log(Level.INFO, "Message to send is :"+message);
	}
	
	/**
	 * Cette méthode permet de communiquer avec le client selon la requete (interested, have ou getpieces)
	 * Deux scénarios possibles 
	 * Soit le client demande le téléchargement d'un fichier 
	 * @see protocoleDownload()
	 * Soit le client demande l'etat d'un fichier
	 * @see protocolInformations
	 */
	private void communicate() {
		PrintWriter p = new PrintWriter(out);

		try {
			Request req = receive();
			send(req);
		} catch (IOException e) {
			e.printStackTrace();
		    p.println("Erreur interne");
		    p.flush();
		} catch (ProtocolException e) {
			e.printStackTrace();
			p.print(e.getMessage());
			p.flush();
		}
		finally {
			disconnect();
		}
	}
	
	public void run() {
		log.log(Level.INFO, Thread.currentThread().getName());
		this.communicate();
	}
	
	private void disconnect(){
		try {
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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