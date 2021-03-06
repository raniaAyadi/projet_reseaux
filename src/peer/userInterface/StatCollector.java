package peer.userInterface;

import java.util.TimerTask;

import peer.Config;
import peer.storage.FileTracker;


/**
 * Starts every one second, collects some stats like down/up speeds, percentage reached, etc.
 * @author Hmama Adem
 *
 */
public class StatCollector extends TimerTask{

	transient private FileTracker ft; // transient => Gson unExpose
	public int id; // needed by UI for mapping
	public int downSpeed;
	public double percentage;
	//public double upSpeed; // TODO

	
	public StatCollector(FileTracker ft){
		this.ft = ft;
		id = ft.id;
		downSpeed = 0;
		percentage = ft.getPercentage();
	}
	
	public void run(){
		Config.downloadLog.fine("down: " + downSpeed + " \npercentage: " + percentage);
		synchronized (this) {
			if(ft.isSeeding())
				downSpeed = 0;
			else
				downSpeed = ft.resetAndGet();
			percentage =  ft.getPercentage();
		}
	}
	
}
