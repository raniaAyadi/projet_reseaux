

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
			path = MyConfig.downloadPath;
		if(path == null)
			path = ".";
		FileTracker ft = new FileTracker(fileName, size, pieceSize, key, path);
		ApplicationContext.fileTrackers.put(ft.getKey(), ft);
		Thread th = new Thread(new FileDownloader(ft));
		ApplicationContext.fileDownloaders.put(ft.getKey(), th);
		th.start();
	}
	
	public static void listFilesOnNetwork(){
		// TODO 
	}
	
	public static void getCurrentStats(){
		// TODO
	}
	
	
	/**
	 * Stop downloading the file, get it off my download list
	 */
	public static void stopLeech(){
		// TODO
	}
	public static void pauseLeech(String key){
		if(!ApplicationContext.fileDownloaders.containsKey(key)){
			System.out.println("requesting suspend for a non existing key + "+ key); // jsut for debug
			return;
		}
		ApplicationContext.fileDownloaders.get(key).suspend();
	}
	
	public static void resumeLeech(String key){
		if(!ApplicationContext.fileDownloaders.containsKey(key)){
			System.out.println("requesting resume for a non existing key + "+ key); // jsut for debug
			return;
		}
		ApplicationContext.fileDownloaders.get(key).resume();
	}
	
	// user calls this method, gets file metadata from the file tracker, then starts download using startLeech
	public static void searchFile(){
		// TODO:  queries to find target file key  
	}
	
}
