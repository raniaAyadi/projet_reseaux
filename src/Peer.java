
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

	
	// TODO: integrate these in the config 
	public static Map<String, FileTracker> fileTrackers;
	public static String metaPath;
	public static String downloadPath;
	public static String uploadPath;
	public static TrackerConnection trackerConnection;

	static {
		fileTrackers = new HashMap<>();

		// TODO: check for it in the configuration file, else default value is
		// ./.meta
		// in this case check for this directory
		// and if it doesnt exit, create it
		metaPath = "./.meta";
		File fl = new File(metaPath);
		if (!(fl.exists() && fl.isDirectory())) {
			System.out.println("peer: static: error: metapath");
			System.exit(0);
		}

		// TODO: check for download path in the configuration file, else set it
		// to null
		// the only us of download path is to set the filetracker path whithout
		// promting the user for a path
		downloadPath = "downloads_folder";

		// TODO: check for it in the configuration file
		// if it doesnt exits, then the upload listener will not be activated
		uploadPath = "uploads_folder";
		
		// TODO : different ways to have the tracker details
		trackerConnection = new TrackerConnection("127.0.0.1", 3000);

	}

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
		
	
	
		// init du client
		new Thread(new PersistanceWorker()).start();
		
		// TODO: check for the uploadPath, if not null then instanciate a
		// uploadListener thread
		if (uploadPath != null)
			new Thread(new UploadListener()).start();

		
		FakeServer.start(fileTrackers);
  
	}

}
