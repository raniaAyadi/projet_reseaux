import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bootstraps the application by initializing configuration, starting application components according to config, this class is also responsable for managing filetrackers and their conrresponding filedownloaders
 * @author msi
 *
 */
public class ApplicationContext {
	public static  Map<Integer, String > idMapper;
	public static Map<String, FileTracker> fileTrackers;
	public static Map<Integer,StatCollector> statCollectors;
	public static Map<Integer,Timer> timers;
	public static TrackerConnection trackerConnection;
	
	private static int uniqueIdCounter = 0;
	private static Timer periodicAnnounce;
	
	private Logger uploadLog;
	private Logger downloadLog;
	private Logger trackerLog;
	
	// it is synchronized since it can be accessed both by the UI listener (by calling UserAction.startLeech) thread and the main thread (construction of the persistence worker)
	public synchronized static int addFileTracker(FileTracker ft) throws Exception{
		ft.id = uniqueIdCounter++;
		fileTrackers.put(ft.getKey(), ft);
		idMapper.put(ft.id, ft.getKey());
		if(!ft.isSeeding()){
			StatCollector st = new StatCollector(ft);
			statCollectors.put(ft.id,st );
			Timer t = new Timer();
			t.scheduleAtFixedRate(st, 100, 1000);
			timers.put(ft.id, t);
			new Thread(new FileDownloader(ft)).start(); 
		}
	
		return ft.id;
	}
	
	
	/**
	 * returns file tracker with the id "id"
	 * @param id
	 * @return
	 */
	public static FileTracker getById(Integer id){
		if(!idMapper.containsKey(id)){
			return null;
		}
		return fileTrackers.get(idMapper.get(id));
	}
	
	
	public ApplicationContext( String[] args) throws Exception {
		Config.init(args); 
		
		fileTrackers = new HashMap<>();
		statCollectors = new HashMap<>();
		idMapper = new HashMap<>();
		timers = new HashMap<>();
		periodicAnnounce = new Timer();
		setLog();
		
		try{
			trackerConnection = new TrackerConnection(Config.trackerIp, Config.trackerPort);
		}catch(UnknownHostException e){
			System.out.println("Invalid tracker address => '" + Config.trackerIp + ":" + Config.trackerPort+"'");
			System.exit(0); 
		}	
		
		// persist application state
		(new Thread(new PersistanceWorker())).start();
		
		Server server = new Server(Config.listenPort);
		
		// start upload watcher if specified in configuration file
		if(Config.uploadPath != null){
			System.out.println("not null");
			(new Thread(new UploadListener())).start();
		}
		
		//start the server
		server.start();
		
		//start periodic communication
		periodicAnnounce.scheduleAtFixedRate(new PeriodicAnnounce(), 0, Config.updatePeriod);
		
		//(new Thread(new UiServer())).start();

	}
	
	private void setLog() throws SecurityException, IOException {
		downloadLog = Logger.getLogger(Constant.Log.DOWNLOAD_LOG);
		uploadLog = Logger.getLogger(Constant.Log.UPLOAD_LOG);
		trackerLog = Logger.getLogger(Constant.Log.TRACKER_LOG);
		
		FileHandler f1 = new FileHandler("download.log");
		FileHandler f2 = new FileHandler("upload.log");
		FileHandler f3 = new FileHandler("tracker.log");
		
		downloadLog.addHandler(f1);
		uploadLog.addHandler(f2);
		trackerLog.addHandler(f3);
		
		switch (Config.logVerbosity){
		case 1 :
			downloadLog.setLevel(Level.CONFIG);
			uploadLog.setLevel(Level.CONFIG);
			trackerLog.setLevel(Level.CONFIG);
			break;
		case 2 :
			downloadLog.setLevel(Level.FINE);
			uploadLog.setLevel(Level.FINE);
			trackerLog.setLevel(Level.FINE);
		}
	}

}
