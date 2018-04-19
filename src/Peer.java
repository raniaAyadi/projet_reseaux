
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class Peer
 * Un peer se caractèrise par un Server (pour envoyer les fichiers) et un Client (pour le téléchargement)
 */
public class Peer {
	private String configPath; // le chemin relatif du fichier de configuration de Peer
	private Logger log; 		// pour le débogage 
	private Server server;  // l'entité Serveur de Peer
	//private Client client;        // l'entité Client de Peer
	private Config config;	// pour extraire les données de configuration



	/**
	 * Constructeur
	 * 
	 * @param configPath
	 *            le chemin relatif du fichier de configuration de Peer
	 * @throws Exception
	 *             si le fichie n'existe pas
	 */
	public Peer(String configPath) throws Exception {
		//this.configPath = configPath;
		//this.log = Logger.getLogger(this.getClass().getName());
		//this.config = Config.getInstance(this.configPath);
		init();
	}

	/**
	 * lancer le serveur et le client
	 */
	public void start() {
		this.server.start();
		// this.client.start();

	}
	


	private void init() throws Exception {

		// init du serveur
		/*
		Integer portNum = Integer.parseInt((String) config.getField(Constant.Config.SERVER_PORT));
		this.log.log(Level.INFO, "Server portNumber : "+portNum);
		// lecture des propriétés de configuration du client
		this.server = new Server(portNum);
		*/
		
	
		(new Thread(new PersistanceWorker())).start();
	    (new Thread(new UploadListener())).start();

		
		//FakeServer.start(fileTrackers);
  
	}

}
