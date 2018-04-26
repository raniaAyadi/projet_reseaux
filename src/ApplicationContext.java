import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * Bootstraps the application by initializing configuration, starting application components according to config, this class is also responsable for managing filetrackers and their conrresponding filedownloaders
 * @author msi
 *
 */
public class ApplicationContext {
	private static  Map<Integer, String > idMapper;
	public static Map<String, FileTracker> fileTrackers;
	public static Map<Integer,StatCollector> statCollectors;
	public static Map<Integer,Timer> timers;
	public static TrackerConnection trackerConnection;
	private static int uniqueIdCounter = 0;
	
	// it is synchronized since it can be accessed both by the UI listener (by calling UserAction.startLeech) thread and the main thread (construction of the persistence worker)
	public synchronized static int addFileTracker(FileTracker ft) throws Exception{
		ft.id = uniqueIdCounter++;
		fileTrackers.put(ft.getKey(), ft);
		idMapper.put(ft.id, ft.getKey());
		StatCollector st = new StatCollector(ft);
		statCollectors.put(ft.id,st );
		Timer t = new Timer();
		t.scheduleAtFixedRate(st, 100, 1000);
		timers.put(ft.id, t);
		if(!ft.isSeeding()){
			new Thread(new FileDownloader(ft)).start(); 
		}
	
		return ft.id;
	}
	
	public static void removeFileTracker(Integer id) {
		fileTrackers.remove(idMapper.get(id));
		idMapper.remove(id);
		// TODO: cordinate this with UserAction.removeFile()
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
		trackerConnection = new TrackerConnection(Config.trackerIp, Config.trackerPort);
		Server server = new Server(Config.listenPort);
		
		// persist application state
		(new Thread(new PersistanceWorker())).start();
		
		// start upload watcher if specified in configuration file
		if(Config.uploadPath != null)
			(new Thread(new UploadListener())).start();
		
		//start the server
		(new Thread(server)).start();

	}



}
