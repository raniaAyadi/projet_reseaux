import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe Server
 * Server h�rite Thread pour s'�x�cuter en parall�le avec un Client
 * un server se caract�rise un num�ro de port, sur lequel il est en �coute permanente 
 */

public class Server implements Runnable {
	
	private ServerSocket serverSocket;        // le socket de serveur
	private Integer portNum;                  // pass� en param�tre d�s l'initialisation
	private Logger log;                       // pour le d�bogage
	private List<ServerThread> threadList ;   // enregistrer la liste des thread associ� � chaque connexion (client)
	
	
	/**
	 * Constructeur
	 * @param portNum num�ro de port
	 */
	public Server(Integer portNum){
		this.log = Logger.getLogger(this.getClass().getName());
		this.portNum = portNum;
		this.threadList = new ArrayList<ServerThread>(); 
		listen();
	}
		
	/**
	 * Initialisation du serverSocket
	 */
	private void listen() {
		try {
			serverSocket = new ServerSocket(this.portNum);
			log.log(Level.INFO,"Listening on port "+this.portNum);
		} catch (IOException e) {
			log.log(Level.WARNING,"Failed listening on port "+this.portNum);
			e.printStackTrace();
		}
	}
	
	/**
	 * Etablissement d'une connexion en passant la socket � un thread traitant (ServerThread)
	 */
	private void connect(){
		Socket socket;
		while(true){
			try {
				socket = this.serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket, threadList.size()+1);
				threadList.add(serverThread);
				serverThread.start();
			} catch (IOException e) {
				log.log(Level.WARNING,"Failed connection");
				e.printStackTrace();
			}	
			
		}
	}
	
	/**
	 * red�finition de la m�thode run de Thread pour lancer le thread
	 */
	public void run(){
		log.log(Level.INFO, Thread.currentThread().getName());
		connect();
	}
}