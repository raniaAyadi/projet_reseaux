

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
	
	public static void listFilesOnNetwork(){
		// TODO 
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
	
	// user calls this method, gets file metadata from the file tracker, then starts download using startLeech
	public static void searchFile(){
		// TODO: queries to find target file key  
		// TODO: first implement response object
	}
	
}
