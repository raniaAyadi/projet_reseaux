import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * On associe à chaque piece non téléchargée identifié par son numéro la liste de Peer la possédant
 * Cette est met à jour pèriodiquement
 */
public class Swarm extends TimerTask{
	
	private List<Piece> pieces;
	private FileTracker fileTracker;
	private Set<PeerConnection> peerConnection;
	private Timer t;

	public Swarm(FileTracker fileTracker, Set<PeerConnection> peerConnection, Timer t) {
		this.fileTracker = fileTracker;
		this.peerConnection = peerConnection;
		this.t = t;
		init();
		
		setSeederPiece();
	}
	
	private void init() {
		this.pieces = new ArrayList<>();

		int size = fileTracker.getBuffermap().length();
		
		for(Integer i=0; i<size; i++) {
			if(!fileTracker.has(i)) {
				pieces.add(new Piece(i, new ArrayList<>()));

			}
		}
		
	}
		
	synchronized public Piece selectRarestPiece() {
		Collections.sort(pieces);
		Piece p = null;
		for(Piece x : pieces) {
			p = x;
			break;
		}
		if(p != null)
			pieces.remove(p);
		
		return p;
	}
	
	synchronized private void setSeederPiece() { 
		for(Piece p : pieces) {
			for(PeerConnection pc : peerConnection) {
				String bufferMap = pc.getBufferMap();
				if(bufferMap.charAt(p.getIndex()) == '1') {
					p.addPeer(pc);
				}
			}
	
		}
	}

	@Override
	public void run() {
		if(fileTracker.isSeeding()) {
			return;
		}
			
		for(PeerConnection p : peerConnection) {
			String key = fileTracker.getKey();
			String mine = fileTracker.getBuffermap();
			
			PeerConnection pc;
			try {
				pc = new PeerConnection(p, fileTracker);
				pc.have(mine, key);
				setSeederPiece();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		if(fileTracker.isSeeding()) {
			System.out.println("Swarm cancel the HAVE Task");
			t.cancel();
		}
	}
	
}
