import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Classe ServerThread
 * ServerThread h�rite la classe Thread
 * un serverThread est associ� � une connexion particuli�re identifi�e par un socket pass� en param�tre
 */

public class ServerThread extends Thread {
	private Socket socket ;     
	private Integer numClient;    
	private Logger log;           
	PrintWriter out;
	BufferedReader in;
	String lastMessage;       // Utile pour relire le dernier message sans attendre le flux d'entr�e
	/**
	 * 
	 * @param socket docket du client
	 * @param numClient le num�ro du client
	 * @throws IOException
	 */
	public ServerThread(Socket socket, Integer numClient) throws IOException{
		this.socket = socket;
		this.numClient = numClient;
		
    	this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(this.socket.getOutputStream());

		this.log = Logger.getLogger(ServerThread.class.getName());
		this.log.log(Level.INFO,this+ "is connected");
	}
	
	/**
	 * 
	 * @param className pour instancier la classe concr�te en applelant le fabriquant
	 * @param readOver = true pour imposer la relecture de flux, sinon l'attente d'un nouveau message
	 * @return instance de la classe className
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private Request receive(String className, boolean readOver) throws IOException, ProtocolException{
		log.log(Level.INFO,className);
		String message;
		
		if(readOver == true) {
			if(lastMessage == null) {
				throw new IOException("Pas de message � relire !");
			}
			message = lastMessage;
		}
		else {
			message = in.readLine();
			log.log(Level.INFO, "Message received is : " + message);
			this.lastMessage = message;
		}
		
		Request req = RequestFactory.createRequest(className, message);
		if(req == null) {
			//A pr�ciser l'exception (ClassNotFoundEception, etc)
			throw new IOException();
		}
		
		return req;
	}
	
	private void send(String className, Request req) throws IOException, ProtocolException {
		Response res = ResponseFactory.createResponse(className, req.getFields());
		String message = res.getMessage();
		log.log(Level.INFO, "Message to send is :"+message);

		out.println(message);
		out.flush();
	}
	
	/**
	 * Cette m�thode permet de communiquer avec le client selon sa requete d'initialisation
	 * Deux sc�narios possibles 
	 * Soit le client demande le t�l�chargement d'un fichier 
	 * @see protocoleDownload()
	 * Soit le client demande l'etat d'un fichier
	 * @see protocolInformations
	 */
	private void communicate() {
		Request req = null;
		
			try {
				req = receive(InitRequestServer.class.getName(), false);
				this.protocolInterested(req);
			} catch (IOException e) {
				e.printStackTrace();
			    this.out.println("Erreur interne");
			    out.flush();
			    disconnect();
			} catch (ProtocolException e) {
				e.printStackTrace();
				try {
					req = receive(HaveRequestServer.class.getName(), true);
					this.protocolInformations(req);
				} catch (IOException e1) {
					e1.printStackTrace();
				    this.out.println("Erreur interne");
				    out.flush();
				    disconnect();
				} catch (ProtocolException e1) {
					e1.printStackTrace();
					this.out.println(e1.getMessage());
					this.out.flush();
					req = null;
					try{
						req = receive(GetRequestServer.class.getName(), true);
						this.protocolDownload(req);
					}
					catch (IOException e2) {
						e1.printStackTrace();
					    this.out.println("Erreur interne");
					    out.flush();
					    disconnect();
					} catch (ProtocolException e2) {
						e1.printStackTrace();
						this.out.println(e2.getMessage());
						this.out.flush();
						req = null;
					}
				}
			}
	}
	

	private void protocolDownload(Request req) {
		try {			
			this.send(GetResponseServer.class.getName(), req);
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
			disconnect();
		}
		

	}
	
	private void protocolInformations(Request req) {
		try {			
			this.send(HaveResponseServer.class.getName(), req);
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
			disconnect();
		}
		

	}
	

	private void protocolInterested(Request req) {
		try {			
			this.send(InitResponseServer.class.getName(), req);
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
			disconnect();
		}
		

	}
	
	public void run() {
		log.log(Level.INFO, Thread.currentThread().getName());
		this.communicate();
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