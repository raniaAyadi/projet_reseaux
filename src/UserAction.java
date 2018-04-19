
public class UserAction {

	
	public static void startLeech(String fileName,long size,int pieceSize,String key,String path) throws Exception{	
		FileTracker ft = new FileTracker(fileName, size, pieceSize, key, path);
		ApplicationContext.fileTrackers.put(ft.getKey(), ft);
		(new Thread(new FileDownloader(ft))).start();
	}
	
	public static void stopLeech(){
		// TODO
	}
	public static void pauseLeech(){
		// TODO
	}
	
	// user calls this method, gets file metadata from the file tracker, then starts download using startLeech
	public static void searchFile(){
		// TODO:  queries to find target file key  
	}
	
}
