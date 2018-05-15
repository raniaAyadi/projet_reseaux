package peer.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.TimerTask;

import peer.Config;
import peer.server.ProtocolException;

/**
 * Classe PeriodicAnnounce
 * Une instance de PeriodicAnnounce est appellee perodiquement pour notifier le tracker de l etat de Peer
 */

public class PeriodicAnnounce extends TimerTask{
	private TrackerConnection tracker;
	
	public PeriodicAnnounce() throws UnknownHostException, IOException, ProtocolException {
		super();
		this.tracker = new TrackerConnection(Config.trackerIp, Config.trackerPort, false);
	}
	
	@Override
	public void run() {
		try {
			Config.generalLog.fine("update tracker");
			this.tracker.announce();
		} catch (IOException | ProtocolException e) {
			Config.generalLog.warning("failed to update tracker");
		}
	}

}
