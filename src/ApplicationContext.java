import java.util.HashMap;
import java.util.Map;

/**
 * holds global variables, like filetrackers, bootstraps the applicatio according to MyConfig
 * @author msi
 *
 */
public class ApplicationContext {
	public static Map<String, FileTracker> fileTrackers;
	public static TrackerConnection trackerConnection;
	
	
	public ApplicationContext( String[] args) throws Exception {
		MyConfig.init(args); 
		fileTrackers = new HashMap<>();
		trackerConnection = new TrackerConnection(MyConfig.trackerIp, MyConfig.trackerPort);
		
		// persist application state
		(new Thread(new PersistanceWorker())).start();
		
		// start upload watcher if specified in configuration file
		if(MyConfig.uploadPath != null)
			(new Thread(new UploadListener())).start();
		
		// TODO: start the server

	}
}
