import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This worker thread persists application state throw serializing the
 * filetracker objects and saving them to disk Second role is a feature, when
 * activated, the worker thread scan for untracked files in the download path
 * These files well become tracked, and the application will start the seed
 * these files to activate the feature, user must setup the Configuration file
 * accordinly
 * 
 * @author Adem Hmama
 * @version 1.0
 * 
 */
public class PersistanceWorker implements Runnable {

	private int interval;
	private boolean listen; // start seeding new files in the download directory
	private List<FileTracker> fileTrackers;

	public PersistanceWorker(List<FileTracker> fileTrackers) {
		this.fileTrackers = fileTrackers;
		// TODO: get the update frequency interval from the config file
		interval = 1000 * 5;
		// TODO: get the value from config file, if not found, set up a default
		// value
		listen = true;

		// reload filetrackers from disk
		// TODO: get download path from config
		String downloadPath = "C:\\Users\\msi\\Desktop\\app_downloads";
		String metaPath = downloadPath + File.separator + ".meta";
		File metaDir = new File(metaPath);
		for (File curr : metaDir.listFiles()) {
			if (curr.isDirectory())
				continue;
			// TODO: more checking : check if the file has its own corresponding
			// file in the download path
			// ignore it if it doesnt end with .ser
			FileTracker ft = null;

			try {
				FileInputStream fileIn = new FileInputStream(curr.getAbsolutePath());
				ObjectInputStream in = new ObjectInputStream(fileIn);
				ft = (FileTracker) in.readObject();
				in.close();
				fileIn.close();
			} catch (IOException i) {
				i.printStackTrace();
				return;
			} catch (ClassNotFoundException c) {
				System.out.println("FileTracker class not found :)");
				c.printStackTrace();
				return;
			}
			this.fileTrackers.add(ft);
		}
	}

	@Override
	public void run() {

		while (true) {

			// print all current tracked files
			System.out.println("tracked files:");
			for (FileTracker fl : fileTrackers) {
				System.out.println(fl.getFileName());
				System.out.println("piece size: " + fl.getPieceSize());
				System.out.println("size: " + fl.getSize());
				System.out.println("key: " + fl.getKey());
				fl.printBufferMap();
			}

			// sleep
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (FileTracker fileTracker : fileTrackers) {
				// serialize file tracker and store it in the .meta folder
				try {
					// TODO: get download path from config
					String downloadPath = "C:\\Users\\msi\\Desktop\\app_downloads";
					FileOutputStream fileOut = new FileOutputStream(downloadPath + File.separator + ".meta"
							+ File.separator + fileTracker.getFileName() + ".ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(fileTracker);
					out.close();
					fileOut.close();
				} catch (IOException i) {
					i.printStackTrace();
				}
			}

			if (listen) {
				Set<String> metaFiles = new HashSet<>();
				// TODO: get download path from config
				String downloadPath = "C:\\Users\\msi\\Desktop\\app_downloads";
				String metaPath = downloadPath + File.separator + ".meta";
				File metaDir = new File(metaPath);
				for (File meta : metaDir.listFiles()) {
					if (meta.isDirectory())
						continue;
					String fileName = meta.getName().substring(0, meta.getName().lastIndexOf("."));
					metaFiles.add(fileName);
				}
				File downloadDir = new File(downloadPath);

				for (File fl : downloadDir.listFiles()) {
					if(fl.isDirectory())
						continue;
					if (!metaFiles.contains(fl.getName())) {
						// init new filetracker
						try {
							FileTracker newFileTracker = new FileTracker(fl.getName());
							fileTrackers.add(newFileTracker);
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				// TODO: scan download path (ignore the .meta folder), if you
				// find an untracker file, create the corresponding filetracker
				// and start seeding it (meaning add it to the list of
				// filetrackers)
			}

		}

	}

}
