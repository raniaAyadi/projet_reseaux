package peer.storage;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import peer.Config;
import peer.userInterface.UserAction;

/**
 *
 * When activated, this thread listens for any changes on the upload directory,
 * in case a new file was added to the directory a new file Tracker is
 * instantiated, and the application starts automatically seeding that file
 *
 * @author Adem Hmama
 * @version 1.0
 *
 */
public class UploadListener implements Runnable {

	@Override
	public void run() {
		Set<String> added = new HashSet<>();
		while (true) {
			Set<String> metaFiles = new HashSet<>();
			File metaDir = new File(Config.metaPath);
			for (File meta : metaDir.listFiles()) {
				if (meta.isDirectory())
					continue;
				String fileName = meta.getName().substring(0, meta.getName().lastIndexOf("."));
				metaFiles.add(fileName);
			}

			File uploadDir = new File(Config.uploadPath);
			for (File fl : uploadDir.listFiles()) {
				if (fl.isDirectory())
					continue;
				if (!metaFiles.contains(fl.getName())) {
					if (!added.contains(fl.getName())) {
						try {
							UserAction.startSeed(fl.getAbsolutePath());
							added.add(fl.getName());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
