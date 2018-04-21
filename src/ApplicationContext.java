import java.util.HashMap;
import java.util.Map;

/**
 * Bootstraps the application by initializing configuration, starting application components according to config, this class is also responsable for managing filetrackers and their conrresponding filedownloaders
 * @author msi
 *
 */
public class ApplicationContext {
	private static  Map<Integer, String > idMapper;
	public static Map<String, FileTracker> fileTrackers;
	public static Map<Integer, FileDownloader> fileDownloaders; // needed for pause/resume functionality
	public static TrackerConnection trackerConnection;
	private static int uniqueIdCounter = 0;
	
	// it is synchronized since it can be accessed both by the UI listener (by calling UserAction.startLeech) thread and the main thread (construction of the persistence worker)
	public synchronized static void addFileTracker(FileTracker ft) throws Exception{
		ft.id = uniqueIdCounter++;
		fileTrackers.put(ft.getKey(), ft);
		idMapper.put(ft.id, ft.getKey());
		if(!ft.isSeeding()){
			FileDownloader fd = new FileDownloader(ft);
			fileDownloaders.put(ft.id, fd);
			new Thread(fd).start();
		}
	}
	
	public static void removeFileTracker(Integer id) {
		fileTrackers.remove(idMapper.get(id));
		idMapper.remove(id);
	}
	
	
	public ApplicationContext( String[] args) throws Exception {
		MyConfig.init(args); 
		fileTrackers = new HashMap<>();
		fileDownloaders = new HashMap<>();
		idMapper = new HashMap<>();
		trackerConnection = new TrackerConnection(MyConfig.trackerIp, MyConfig.trackerPort);
		Server server = new Server(MyConfig.listenPort);
		
		// persist application state
		(new Thread(new PersistanceWorker())).start();
		
		// start upload watcher if specified in configuration file
		if(MyConfig.uploadPath != null)
			(new Thread(new UploadListener())).start();
		
		//start the server
		(new Thread(server)).start();
	}



}
