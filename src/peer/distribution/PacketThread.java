package peer.distribution;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Logger;

import peer.server.ProtocolException;
import peer.server.Request;
import peer.server.RequestFactory;


public class PacketThread implements Runnable {
	private DatagramPacket packet;
	private Logger log;
	
	public PacketThread(DatagramPacket packet) {
		this.packet = packet;
		this.log = Logger.getLogger(this.getClass().getName());
	}

	private Request receive() throws IOException, ProtocolException{
		String message = new String(packet.getData(), 0, packet.getLength()-1);
		log.info("Receive "+packet.getLength()+" */* "+message+"/*"+packet.getSocketAddress());		
		
		Request req = RequestFactory.createRequest(message, this.packet.getAddress());
		if(req == null) {
			//A préciser l'exception (ClassNotFoundEception, etc)
			throw new IOException();
		}	
		return req;
	}
	
	private void communicate() throws IOException, ProtocolException {
		Request req = receive();
		send(req);
	}
	
	private void send(Request req) throws ProtocolException {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	    
		NeighbourdResponse res = new NeighbourdResponse(req.getFields());
		res.setMessage();
		String message = res.getMessage();
	    byte[] buf = message.getBytes();
	 
	    DatagramPacket packet = new DatagramPacket(buf, buf.length, this.packet.getAddress(), 48772);
	    log.info(this.packet.getAddress()+"*/"+this.packet.getPort());
	    try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    socket.close();
	}
	
	@Override
	public void run() {
		try {
			communicate();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
	}
	
}
