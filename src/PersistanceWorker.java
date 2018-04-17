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
 * filetracker objects and saving them to disk on the .meta folder
 *
 * The second role is a feature, when
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


	public PersistanceWorker() throws Exception {

		// TODO: get the update frequency interval from the config file
		interval = 1000 * 5;


		// reload filetrackers from disk
		File metaDir = new File(Peer.metaPath);
		for (File curr : metaDir.listFiles()) {
			if (curr.isDirectory())
				continue;
			// TODO: more checking : check if the file has its own corresponding
			// file in the download path
			// ignore it if it doesn't end with .ser
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
			Peer.fileTrackers.put(ft.getKey(), ft);
			BitSet b = ft.getBufferMap();
			boolean seed = true;
			for(int i =0;i< ft.getNumberPieces();i++){
				if(!b.get(i))
					seed = false;
			}
			if(!seed){
				(new Thread(new FileDownloader(ft))).start();
			}
		}
	}

	@Override
	public void run() {

		while (true) {
			// TODO: handel this in a better way, is the metaPath crutial, can this thread be shut down ??, should this thread
			File fl = new File(Peer.metaPath);
			if(!fl.exists() || !fl.isDirectory()){ // user fucked up the meta directory while the app is running
				System.err.println("persistance context : error .meta path not valid");
				System.exit(0);
			}

			// print all current tracked files
			System.out.println("tracked files:");
			for (Map.Entry<String, FileTracker> entry : Peer.fileTrackers.entrySet()) {
				FileTracker ft = entry.getValue();
				System.out.println(ft.getFileName());
				System.out.println("piece size: " + ft.getPieceSize());
				System.out.println("size: " + ft.getSize());
				System.out.println("key: " + ft.getKey());
				System.out.println("key: " + ft.getKey());
				ft.printBufferMap();
			}

			// sleep
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (Map.Entry<String, FileTracker> entry : Peer.fileTrackers.entrySet()) {
				FileTracker fileTracker = entry.getValue();
				if(fileTracker.isSeeding())
					continue;
				// serialize file tracker and store it in the .meta folder
				try {
					FileOutputStream fileOut = new FileOutputStream(Peer.metaPath + File.separator + fileTracker.getFileName() + ".ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(fileTracker);
					out.close();
					fileOut.close();
				} catch (IOException i) {
					i.printStackTrace();
				}
			}

		}

	}

}
