import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This worker thread persists application state throw serializing the
 * filetracker objects and saving them to disk on the metadata folder
 *
 *
 * @author Hmama Adem
 * @version 1.0
 *
 */
public class PersistanceWorker implements Runnable {

	private int interval;


	public PersistanceWorker() throws Exception {

		// TODO: get the update frequency interval from the config file
		interval = 1000 * 5;


		// reload filetrackers from disk
		File metaDir = new File(Config.metaPath);
		for (File curr : metaDir.listFiles()) {
			if (curr.isDirectory())
				continue;
			// TODO: more error handling 
			// like ignore it if it doesn't end with .ser
			FileTracker ft = null;

			try {
				FileInputStream fileIn = new FileInputStream(curr.getAbsolutePath());
				ObjectInputStream in = new ObjectInputStream(fileIn);
				ft = (FileTracker) in.readObject();
				in.close();
				fileIn.close();
				System.out.println(ft.getKey());
				ApplicationContext.addFileTracker(ft);	
			} catch (IOException i) {
				i.printStackTrace();
				return;
			} catch (ClassNotFoundException c) {
				System.out.println("FileTracker class not found :)");
				c.printStackTrace();
				return;
			}
		}
	}

	@Override
	public void run() {

		while (true) {
			// TODO: handel this in a better way, is the metaPath crutial, can this thread be shut down ??, should this thread
			File fl = new File(Config.metaPath);
			if(!fl.exists() || !fl.isDirectory()){ // user fucked up the meta directory while the app is running
				System.err.println("persistance context : error .meta path not valid");
				System.exit(0);
			}

			// log all current tracked files
			System.out.format("\n\n  %-15s%-10s%-15s%-25s\n", "filename", "size", "piece-size", "key");
			System.out.println("  ==============================================================");
			for (Map.Entry<String, FileTracker> entry : ApplicationContext.fileTrackers.entrySet()) {
				FileTracker ft = entry.getValue();
				System.out.format("  %-15s%-10s%-5s%-20s\n", ft.getFileName(), ft.getSize(), ft.getPieceSize(),
						ft.getKey());

			}

			// sleep
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (Map.Entry<String, FileTracker> entry : ApplicationContext.fileTrackers.entrySet()) {
				FileTracker fileTracker = entry.getValue();
				// TODO: if file is seeding and it has been persisted at least once after it got to this state, dont persist it again
				// add boolean in file tracker
				
				
				// serialize file tracker and store it in the .meta folder
				try {
					FileOutputStream fileOut = new FileOutputStream(Config.metaPath + File.separator + fileTracker.getFileName() + ".ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					synchronized (fileTracker) {
						out.writeObject(fileTracker);
					}
					out.close();
					fileOut.close();
				} catch (IOException i) {
					i.printStackTrace();
				}
			}

		}

	}

}
