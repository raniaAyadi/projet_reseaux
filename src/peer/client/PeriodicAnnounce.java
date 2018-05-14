package peer.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.TimerTask;

import peer.Config;
import peer.server.ProtocolException;

public class PeriodicAnnounce extends TimerTask{
	private TrackerConnection tracker;
	
	public PeriodicAnnounce() throws UnknownHostException, IOException, ProtocolException {
		super();
		this.tracker = new TrackerConnection(Config.trackerIp, Config.trackerPort, false);
	}
	
	@Override
	public void run() {
		try {
			this.tracker.announce();
		} catch (IOException | ProtocolException e) {
			e.printStackTrace();
		}
	}

}
