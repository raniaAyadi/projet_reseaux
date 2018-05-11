
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe Server
 * Server hérite Thread pour s'éxécuter en parallèle avec un Client
 * un server se caractèrise un numéro de port, sur lequel il est en écoute permanente 
 */

public class Server extends Thread {
	
	private ServerSocket serverSocket;        // le socket de serveur
	private Integer portNum;                  // passé en paramètre dés l'initialisation
	private Logger log;                       // pour le débogage
	private List<ServerThread> threadList ;   // enregistrer la liste des thread associé à chaque connexion (client)
	private ExecutorService executor;           // gérer les thread, notamment la pool de thread
	
	/**
	 * Constructeur
	 * @param portNum numéro de port
	 */
	public Server(Integer portNum){
		this.log = Logger.getLogger(this.getClass().getName());
		this.portNum = portNum;
		this.threadList = new ArrayList<ServerThread>(); 
		
		executor = Executors.newFixedThreadPool(Config.poolSize);

		listen();
	}
	
	// TODO public Seeder(File* f);
	
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
	 * Etablissement d'une connexion en passant la socket à un thread traitant (ServerThread)
	 */
	private void connect(){
		Socket socket;
		while(true){
			try {
				socket = this.serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket, threadList.size()+1);
				threadList.add(serverThread);
				executor.execute(serverThread);
			} catch (IOException e) {
				log.log(Level.WARNING,"Failed connection");
				e.printStackTrace();
			}	
			
		}
	}
	
	/**
	 * redéfinition de la méthode run de Thread pour lancer le thread
	 */
	public void run(){
		log.log(Level.INFO, Thread.currentThread().getName());
		connect();
	}
}