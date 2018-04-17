
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
 * Class Peer Un peer se caractèrise par un Server (pour envoyer les fichiers)
 * et un Client (pour le téléchargement)
 */

public class Peer {
	private String configPath; // le chemin relatif du fichier de configuration
								// de Peer
	private Logger log; // pour le débogage
	private Server server; // l'entité Serveur de Peer
	// private Client client; // l'entité Client de Peer
	private Config config; // pour extraire les données de configuration

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
		// this.configPath = configPath;
		// this.log = Logger.getLogger(this.getClass().getName());
		// this.config = Config.getInstance(this.configPath);
		init();
	}

	/**
	 * lancer le serveur et le client
	 */
	public void start() {
		this.server.start();
		// this.client.start();

	}
	
	public static String bitsetToString(BitSet b,int size){
		String ret = "";
		for(int i = 0 ; i < size;i ++){
			if(b.get(i)){
				ret+='1';
			}else{
				ret += '0';
			}
		}
		return ret;
	}
	
	public static BitSet stringToBitset(String s) {
		BitSet b = new BitSet(s.length());
		for(int i =0;i < s.length();i++	){
			if(s.charAt(i) == '1')
				b.set(i, true);
			else
				b.set(i,false);
		}
		return b;
	}

	private void init() throws Exception {

		/*
		 * Integer portNum = Integer.parseInt((String)
		 * config.getField(Constant.Config.SERVER_PORT));
		 * this.log.log(Level.INFO, "Server portNumber : "+portNum); // lecture
		 * des propriétés de configuration du client
		 * 
		 * 
		 * PersistanceWorker pworker = new PersistanceWorker(); Thread th = new
		 * Thread(pworker); th.start(); // TODO: initiate one FileClient for
		 * each file tracker // TODO: check for the uploadPath, if not null then
		 * instanciate a uploadListener thread
		 * 
		 * //this.server = new Server(portNum);
		 */

		new Thread(new PersistanceWorker()).start();
		// TODO: initiate one FileClient for each file tracker
		// TODO: check for the uploadPath, if not null then instanciate a
		// uploadListener thread

		if (uploadPath != null)
			new Thread(new UploadListener()).start();

		
		
		
		
		
	
		
		// fake file server
		ServerSocket s = new ServerSocket(3001);

		while (true) {
			Socket soc = s.accept();
			System.out.println("client connected");

			BufferedReader plec = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			OutputStream os = soc.getOutputStream();
			PrintWriter pred = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)), true);

			
			String request = plec.readLine();
			String res= new String("");
			String[] req = request.split(" ");
			if(req[0].equals("interested")){
				if(req.length != 2){
					res = "only two arguments in this type of request";
				}else{
					String requestedKey = req[1];
					if(fileTrackers.containsKey(requestedKey)){
						// write directly
						pred.write("have " + requestedKey + " ");
						
						
						// write the buffermap
						FileTracker resp = fileTrackers.get(requestedKey);
						BitSet buffermap = resp.getBufferMap();
						pred.write(bitsetToString(buffermap, resp.getNumberPieces()));
						pred.flush();
						soc.close();
						continue;
					}else{
						res = "i dont serve this file";
					}
				}
			}else if(req[0].equals("getpieces")){
				String key = req[1];
				if(!fileTrackers.containsKey(key)){
					pred.write("i dont have this key man");
					pred.flush();
					soc.close();
					continue;
				}
				String indexes = request.substring(request.indexOf('['));
				if(indexes.length() == 2){
					res = "data " + key + "[]";
				}else{
					String[] ls = indexes.substring(1, indexes.length()-1).split(" ");
					pred.write("data " + key+" [");
					pred.flush();
					for(int i =0;i< ls.length; i++){
						pred.write(ls[i] + ":" );
						pred.flush();
						int pieceIndex = Integer.parseInt(ls[i]);
						if(pieceIndex >= fileTrackers.get(key).getSize()){
							// invalid index requested 
							System.out.println("peer requested an invalid index : " + pieceIndex);
							System.out.println("returing a string of spaces");
						}
						byte[] piece = fileTrackers.get(key).getPiece(pieceIndex);
						os.write(piece);
						if(i != ls.length-1){
							pred.write(" ");
							pred.flush();
						}	
					}
					pred.write("]");
					pred.flush();
					soc.close();
					continue;
				}
			}else{
				res = "unregonized command";
			}
		
			

			pred.write(res);
			pred.flush();
			soc.close();
		}
  
	}

}
