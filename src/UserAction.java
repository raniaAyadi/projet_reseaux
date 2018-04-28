import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class holds user actions, all methods must be exposed over tcp and mapped to front end 
 * @author msi
 *
 */
public class UserAction {

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
	public static void startSeed(String fullFilePath) throws Exception{
		File fl = new File(fullFilePath);
		if(!fl.exists())
			throw new FileNotFoundException();
		if(fl.isDirectory())
			throw new Exception("specified path corresponds to a directory, only files are supported");
		// TODO : test if file exists on the network, if so tell him to change the name of the file
		//if(!searchFiles(fl.getName(), null, null).isEmpty())
		//	throw new FileExistsOnNetworkException();
		FileTracker ft = new FileTracker(fl);
		ApplicationContext.addFileTracker(ft);
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
		return ApplicationContext.trackerConnection.look(fileName, minSize, maxSize);
	}
	
	public static void getCurrentStats(){
		// TODO: first: implement the FileStat data structure
	}
	
	
	/**
	 * Stop leeching/seeding 
	 */
	public static void removeFile(Integer id){
		// TODO
		// will i need maps for file downlaoder and stat collector in appContext or can i do it otherwise (through filetracker ?)
		// remove filetracker reference and set some attribute indicating finish state, threads automatically exits and Filetracker becomes
		// object of garbage collector, in this case you should update persistanceWorker to take that variable into consideration (do not 
		// persist any more
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
