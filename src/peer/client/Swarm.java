package peer.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import peer.storage.FileTracker;
import peer.storage.Piece;


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
	
	synchronized public List<Piece> selectRamdomPiece(int nb){
		int index[] = new int[nb];
		List<Piece> ret = new ArrayList<>(nb);

		for(int j=0; j<nb; j++) {
			int i = (int)(Math.random()*nb);
			index[j] = i;
		}
		
		Arrays.sort(index);
		int i = 0;
		int j = 0;
		for(Piece p : pieces) {
			if(i == index[j]) {
				ret.add(p);
				j++;
			}
			i++;
		}
		
		for(Piece p :ret)
			pieces.remove(p);
		
		if(ret.isEmpty())
			return null;
		
		return ret;
	}
		
	synchronized public List<Piece> selectRarestPiece(int nb) {
		Collections.sort(pieces);
		List<Piece> ret = new ArrayList<>(nb);
	
		for(Piece p : pieces) {
			ret.add(p);
			nb--;
			
			if(nb == 0)
				break;
		}
		if(ret.isEmpty())
			return null;
		
		for(Piece p : ret)
			this.pieces.remove(p);
		
		return ret;
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
			t.cancel();
			return;
		}
			
		for(PeerConnection p : peerConnection) {
			String key = fileTracker.getKey();
			String mine = fileTracker.getBuffermap();
			
			PeerConnection pc;
			try {
				pc = new PeerConnection(p, fileTracker);
				pc.have(mine, key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		setSeederPiece();
	}
	
}
