

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
		
		// TODO: locate thread, terminate it (maybe lock on filetracker) , then remove file tracker  
		
		//if(ApplicationContext.fileDownloaders.containsKey(id)){
			//ApplicationContext.fileDownloaders.get(id).stop();
		//}
		//ApplicationContext.removeFileTracker(id);
		// TODO : ask the user if he wants to delete the file from the disk too (in case of non seeded file)	
		// TODO what about when the file is uploaded using the upload listener, should i delete the file from the upload directory ?
		// or is it simply no problem (bug in upload listener fixed by <added> set
	}
	
	public static void pauseLeech(Integer id){
		// TODO : error checking 
		ApplicationContext.getById(id).pause();
	}
	
	public static void resumeLeech(Integer id){
		// TODO: error check
		ApplicationContext.getById(id).resume();
	}
	
	// user calls this method, gets file metadata from the file tracker, then starts download using startLeech
	public static void searchFile(){
		// TODO: queries to find target file key  
		// TODO: first implement response object
	}
	
}
