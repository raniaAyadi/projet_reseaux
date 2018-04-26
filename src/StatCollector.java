import java.util.TimerTask;


/**
 * Starts every one second, collects some stats like down/up speeds, percentage reached, etc.
 * @author Hmama Adem
 *
 */
public class StatCollector extends TimerTask{

	private FileTracker ft;
	public int downSpeed;
	public double percentage;
	//public double upSpeed; // TODO

	
	public StatCollector(FileTracker ft){
		this.ft = ft;	
		downSpeed = 0;
		percentage = ft.getPercentage();
	}
	
	public void run(){

		System.out.println("from stat collector : ");
		System.out.println("down: " + downSpeed + " \npercentage: " + percentage); 
		
		synchronized (this) {
			if(ft.isSeeding())
				downSpeed = 0;
			else
				downSpeed = ft.resetAndGet();
			percentage =  ft.getPercentage();
		}
	}
	
}
