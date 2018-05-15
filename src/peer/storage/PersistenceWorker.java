package peer.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import peer.ApplicationContext;
import peer.Config;

/**
 * This worker thread persists application state throw serializing the
 * FileTracker objects and saving them to disk on the meta-data folder
 *
 *
 * @author Hmama Adem
 * @version 1.0
 *
 */
public class PersistenceWorker implements Runnable {

	private int interval;

	public PersistenceWorker() throws Exception {
		// TODO: set from config
		interval = 1000 * 3;

		// reload filetrackers from disk
		File metaDir = new File(Config.metaPath);
		for (File curr : metaDir.listFiles()) {
			if (curr.isDirectory())
				continue;
			FileTracker ft = null;
			try {
				FileInputStream fileIn = new FileInputStream(curr.getAbsolutePath());
				ObjectInputStream in = new ObjectInputStream(fileIn);
				ft = (FileTracker) in.readObject();
				in.close();
				fileIn.close();
				ApplicationContext.addFileTracker(ft);
			} catch (Exception e) {
				Config.generalLog.warning("Error loading FileTracker <" + curr.getName() + "> metadata from disk");
				return;
			}
		}
	}

	@Override
	public void run() {

		while (true) {

			File fl = new File(Config.metaPath);
			if (!fl.exists() || !fl.isDirectory()) {
				Config.generalLog.severe("Error meta-path not valid");
				System.exit(-1);
			}

			// logFiles(); // debug

			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// persist
			for (Map.Entry<String, FileTracker> entry : ApplicationContext.fileTrackers.entrySet()) {
				FileTracker fileTracker = entry.getValue();
				try {
					FileOutputStream fileOut = new FileOutputStream(
							Config.metaPath + File.separator + fileTracker.getFileName() + ".ser");
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

	private void logFiles() { // debug
		// log all current tracked files
		System.out.format("\n\n  %-15s%-10s%-15s%-25s\n", "filename", "size", "piece-size", "key");
		System.out.println("  ==============================================================");
		for (Map.Entry<String, FileTracker> entry : ApplicationContext.fileTrackers.entrySet()) {
			FileTracker ft = entry.getValue();
			System.out.format("  %-15s%-10s%-5s%-20s\n", ft.getFileName(), ft.getSize(), ft.getPieceSize(),
					ft.getKey());
		}
	}

}
