

/**
 * This class holds user actions, all methods must be exposed over tcp and mapped to front end 
 * @author msi
 *
 */
public class UserAction {

	/**
	 * User must get all these are information from the tracker, except path which he will have to chose
	 * of course process must be simplified when using UI
	 * @param fileName
	 * @param size
	 * @param pieceSize
	 * @param key
	 * @param path where do you want the file to be saved to, if null, checks if download path is set, else download to current directory
	 * @throws Exception
	 */
	public static void startLeech(String fileName,long size,int pieceSize,String key,String path) throws Exception{	
		if(path == null)
			path = Config.downloadPath;
		if(path == null)
			path = ".";
		FileTracker ft = new FileTracker(fileName, size, pieceSize, key, path);
		ApplicationContext.addFileTracker(ft);	
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
		if(ApplicationContext.fileDownloaders.containsKey(id)){
			ApplicationContext.fileDownloaders.get(id).stop();
		}
		ApplicationContext.removeFileTracker(id);
		// TODO : ask the user if he wants to delete the file from the disk too (in case of non seeded file)	
		// TODO what about when the file is uploaded using the upload listener, should i delete the file from the upload directory ?
		// or is it simply no problem (bug in upload listener fixed by <added> set
	}
	
	public static void pauseLeech(Integer id){
		if(!ApplicationContext.fileDownloaders.containsKey(id)){
			System.out.println("requesting suspend for a non existing key + "+ id); // TODO : verbosity controle
			return;
		}
		ApplicationContext.fileDownloaders.get(id).pause();
	}
	
	public static void resumeLeech(Integer id){
		if(!ApplicationContext.fileDownloaders.containsKey(id)){
			System.out.println("requesting resume for a non existing key + "+ id); // just for debug
			return;
		}
		ApplicationContext.fileDownloaders.get(id).resume();
	}
	
	// user calls this method, gets file metadata from the file tracker, then starts download using startLeech
	public static void searchFile(){
		// TODO: queries to find target file key  
		// TODO: first implement response object
	}
	
}
