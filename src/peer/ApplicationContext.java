package peer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Logger;

import peer.client.FileDownloader;
import peer.client.PeriodicAnnounce;
import peer.client.TrackerConnection;
import peer.server.Server;
import peer.storage.FileTracker;
import peer.storage.PersistenceWorker;
import peer.storage.UploadListener;
import peer.userInterface.StatCollector;
import peer.userInterface.UiServer;

/**
 * Bootstraps the application by initializing configuration, starting
 * application components according to config, this class is also responsable
 * for managing filetrackers and their conrresponding filedownloaders
 * 
 * @author msi
 *
 */
public class ApplicationContext {
	public static Map<Integer, String> idMapper;
	public static Map<String, FileTracker> fileTrackers;
	public static Map<Integer, StatCollector> statCollectors;
	public static Map<Integer, Timer> timers;
	public static TrackerConnection trackerConnection;

	private static int uniqueIdCounter = 0;
	private static Timer periodicAnnounce;

	
	/**
	 * Add a new Filetracker, a local id will be set to the fileTracker, (needed for the Interface to make
	 * id based selection instead of key based which is not practical)
	 * it is synchronized since it can be accessed both by the UI listener (by
	 * calling UserAction.startLeech) thread and the main thread (construction
	 * of the persistence worker)
	 * 
	 * @param ft
	 * @return
	 * @throws Exception
	 */
	public synchronized static int addFileTracker(FileTracker ft) throws Exception {
		ft.id = uniqueIdCounter++;
		fileTrackers.put(ft.getKey(), ft);
		idMapper.put(ft.id, ft.getKey());
		if (!ft.isSeeding()) {
			StatCollector st = new StatCollector(ft);
			statCollectors.put(ft.id, st);
			Timer t = new Timer();
			t.scheduleAtFixedRate(st, 100, 1000);
			timers.put(ft.id, t);
			new Thread(new FileDownloader(ft)).start();
		} else {
			Config.uploadLog.fine("The file " + ft.getFileName() + " is shared");
		}
		return ft.id;
	}

	/**
	 * returns file tracker with the id "id"
	 * 
	 * @param id
	 * @return
	 */
	public static FileTracker getById(Integer id) {
		if (!idMapper.containsKey(id)) {
			return null;
		}
		return fileTrackers.get(idMapper.get(id));
	}

	public ApplicationContext(String[] args) throws Exception {
		Config.init(args);
		Logger log = Logger.getLogger(Constant.Log.GENERAL_LOG);
		fileTrackers = new HashMap<>();
		statCollectors = new HashMap<>();
		idMapper = new HashMap<>();
		timers = new HashMap<>();
		periodicAnnounce = new Timer();

		if (Config.version == 0){
			try {
				trackerConnection = new TrackerConnection(Config.trackerIp, Config.trackerPort, true);
			} catch (Exception e) {
				log.severe("Invalid tracker address: '" + Config.trackerIp + ":" + Config.trackerPort + "'");
				System.exit(-1); // application cannot run if unable to resolve
									// tracker address
			}
		}
			
		// Start Application state persistence worker
		(new Thread(new PersistenceWorker())).start();

		// Start the server
		new Server(Config.listenPort).start();

		// Start upload listener if specified in configuration file
		if (Config.uploadPath != null)
			(new Thread(new UploadListener())).start();

		// Start periodic communication Tracker-Peer
		periodicAnnounce.scheduleAtFixedRate(new PeriodicAnnounce(), 0, Config.updatePeriod);

		// Start interface server
		(new Thread(new UiServer())).start();
	}
}
