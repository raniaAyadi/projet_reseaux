import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * This class holds user actions, all methods must be exposed over tcp and mapped to front end 
 * @author msi
 *
 */
public class UserAction {
	private static Logger log = Logger.getLogger(UserAction.class.getName());

	//On suppose que la tracker retourne aprés request look un seul fichier
	public static void download(String fileName,Integer minSize,Integer maxSize, String path) throws Exception{
		List<FileInfo> files = searchFiles(fileName, minSize, maxSize);
		
		log.log(Level.INFO, "size of look-files returned by tracker : "+files.size());
		if(files.isEmpty()) {
			//throw new FileNotAvailableException("File not avaible in network actually");
		}
		
		//TODO size > 1
		FileInfo f = files.get(0);
		startLeech(f.fileName, f.fileSize, f.pieceSize, f.key, path);
	}
	
	/**
	 * 
	 * User must get all these are information from the tracker, except path which he will have to chose
	 * of course process must be simplified when using UI
	 * 
	 * @param fileName
	 * @param size
	 * @param pieceSize
	 * @param key
	 * @param path where do you want the file to be saved to, if null, checks if download path is set, else download to current directory
	 * @return locally generated id 
	 * @throws Exception
	 */
	public static int startLeech(String fileName,long size,int pieceSize,String key,String path) throws Exception{	
		if(path == null)
			path = Config.downloadPath;
		if(path == null)
			path = ".";
		FileTracker ft = new FileTracker(fileName, size, pieceSize, key, path);
		return ApplicationContext.addFileTracker(ft);	
	}
	
	/**
	 * Share new file on the network
	 * @param fullFilePath 
	 * @throws Exception
	 */
	public static int startSeed(String fullFilePath) throws Exception{
		File fl = new File(fullFilePath);
		if(!fl.exists())
			throw new FileNotFoundException();
		if(fl.isDirectory())
			throw new Exception("specified path corresponds to a directory, only files are supported"); // TODO : custom exception
		// TODO : test if file exists on the network, if so tell him to change the name of the file
		//if(!searchFiles(fl.getName(), null, null).isEmpty())
		//	throw new FileExistsOnNetworkException();
		FileTracker ft = new FileTracker(fl);
		return ApplicationContext.addFileTracker(ft);
	}
	
	/**
	 * List all files on the network
	 * @return metadata for all files shared on the network
	 * @throws Exception
	 */
	public static List<FileInfo>  listAll() throws Exception{
		return searchFiles(null, null, null);
	}
	
	/**
	 * Search files on the network, only non null constraints will be considered
	 * @param fileName if set, one or zero entries will be returned 
	 * @param minSize 
	 * @param maxSize
	 * @return list of files on the network corresponding to the selected criteria 
	 * @throws Exception
	 */
	public static List<FileInfo> searchFiles(String fileName,Integer minSize,Integer maxSize) throws Exception{
		List<FileInfo> ret =  ApplicationContext.trackerConnection.look(fileName, minSize, maxSize);
		for(FileInfo f : ret)
			if(ApplicationContext.fileTrackers.containsKey(f.key)) f.managed = true;
			else f.managed = false;
		return ret;
	}
	
	/**
	 * Get all currently managed files (being served or downloaded by the peer)
	 * @return A list of Filetrackers is returned, so its up to the UI exposer to filter needed information 
	 */
	public static List<FileTracker> getManagedFiles(){
		Map<String,  FileTracker> m = ApplicationContext.fileTrackers;
		List<FileTracker> ret = new ArrayList<>();
		for(Map.Entry<String, FileTracker> entry : m.entrySet())
			ret.add(entry.getValue());
		return ret;
	}
	
	/**
	 * Get download/upload stats (percentage, downspeed, upspeed ...), information is mapped to the list of managed files using 
	 * field 'id'
	 * @return 
	 */
	public static List<StatCollector> getStats(){
		Map<Integer,StatCollector> m = ApplicationContext.statCollectors;
		List<StatCollector> ret = new ArrayList<>();
		for(Map.Entry<Integer, StatCollector> entry : m.entrySet())
			ret.add(entry.getValue());
		return ret;
	}
	
	/**
	 * This is not always exposed (consumes bandwidth), gets displayed only on user demand
	 * @param id
	 * @return String formatted BufferMap
	 * @throws NumberFormatException
	 */
	public static String getBufferMap(String id) throws NumberFormatException{ // TODO: throws also FileTracker not found
		return ApplicationContext.getById(Integer.parseInt(id)).getBuffermap();
	}
	
	
	/**
	 * Stop managing the file 
	 */
	public static void removeFile(Integer id){
		// TODO: throw exception if id non valid
		FileTracker ref = ApplicationContext.getById(id);
		Timer t =  ApplicationContext.timers.get(id);
		ApplicationContext.idMapper.remove(id);
		ApplicationContext.fileTrackers.remove(ref.getKey());
		ApplicationContext.timers.remove(id);	
		
		ref.terminate();
		if(t != null)
			t.cancel();
		
		File metafile = new File(Config.metaPath +   File.separator +  ref.getFileName() + ".ser");
		if(metafile.exists())
			metafile.delete();
		if(ref.isSeeding())
			return;
		File fl = new File(ref.getFilePath());
		if(fl.exists())
			fl.delete();
	}
	
	public static void pauseLeech(Integer id){
		// TODO : error checking 
		FileTracker ft = ApplicationContext.getById(id);
		if(ft.isSuspended())
			return;
		ft.pause();
	}
	
	public static void resumeLeech(Integer id){
		// TODO: error check, why two consecutive resumes, pauseLeech doesn't pause.
		FileTracker ft = ApplicationContext.getById(id);
		if(!ft.isSuspended())
			return;
		ft.resume();
	}
	
}
