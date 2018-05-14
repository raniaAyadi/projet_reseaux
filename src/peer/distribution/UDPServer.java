package peer.distribution;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import peer.Config;

public class UDPServer extends Thread{
	private MulticastSocket socket;
	private Logger log;
	
	public UDPServer() {
		this.log = Logger.getLogger(this.getClass().getName());
		
		InetAddress group = null;
		try {
			group = InetAddress.getByName(Config.multicastIP);
			log.info("Group "+group.getAddress().toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.socket = new MulticastSocket(Config.udpPort);
			log.info("Port "+socket.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			socket.joinGroup(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		while(true) {
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
	        try {
				socket.receive(packet);
				(new Thread(new PacketThread(packet))).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
