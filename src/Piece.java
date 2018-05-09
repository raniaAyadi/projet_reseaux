import java.util.List;

public class Piece implements Comparable<Piece>{
	private Integer index;
	private List<PeerConnection> seeders;
	
	public Piece(Integer index, List<PeerConnection> seeders) {
		this.index = index;
		this.seeders = seeders;
	}
	
	public Integer getIndex() {
		return index;
	}
	
	public void addPeer(PeerConnection pc) {
		this.seeders.add(pc);
	}
	
	public List<PeerConnection> getSeeder(){
		return this.seeders;
	}

	@Override
	public int compareTo(Piece p) {
		int diff = this.seeders.size() - p.seeders.size();
		if(diff < 0)
			return 1;
		else if(diff == 0)
			return 0;
		else 
			return 0;
	}

}
