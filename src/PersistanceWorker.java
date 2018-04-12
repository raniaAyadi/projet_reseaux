import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * This worker thread persists application state throw serializing the filetracker objects and saving them to disk
 * Second role is a feature, when activated, the worker thread scan for untracked files in the download path 
 * These files well become tracked, and the application will start the seed these files
 * to activate the feature, user must setup the Configuration file accordinly
 * @author Adem Hmama
 * @version 1.0
 * 
 */
public class PersistanceWorker implements Runnable {

	private int interval;
	private boolean listen; // start seeding new files in the download directory
	private List<FileTracker> fileTrackers;
	
	
	public PersistanceWorker(List<FileTracker> fileTrackers){
		// TODO: get the update frequency interval from the config file
		int interval = 1000 * 60;
		// TODO: get the value from config file, if not found, set up a default value
		listen = true;
		this.fileTrackers = fileTrackers;
	}
	
	@Override
	public void run(){
		while(true){
			for (FileTracker fileTracker : fileTrackers) {
				// serialize file tracker and store it in the .meta folder
				try {
					 // TODO: get download path from config
					 String downloadPath = "C:\\Users\\msi\\Desktop\\app_downloads";
			         FileOutputStream fileOut =
			         new FileOutputStream(downloadPath + File.separator +".meta" + File.separator+ fileTracker.getFileName() + ".ser");
			         ObjectOutputStream out = new ObjectOutputStream(fileOut);
			         out.writeObject(fileTracker);
			         out.close();
			         fileOut.close();
			         System.out.printf("Serialized data is saved");
			      } catch (IOException i) {
			         i.printStackTrace();
			      }
			}
			
			if(listen){
				// TODO: scan download path (ignore the .meta folder), if you find an untracker file, create the corresponding filetracker
				// and start seeding it (meaning add it to the list of filetrackers)
			}
			
			// sleep
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
