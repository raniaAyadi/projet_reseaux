import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestPeerConnection {
	
	public static void  printbyte(byte[] b){
		for(int i = 0;i < b.length;i++){
			char c = (char ) b[i];
			System.out.print(c);
		}
		System.out.println("");
	}

	public static void main(String[] args) throws Exception {
		UserAction.startLeech("README.md", 19, 4, "s9j8Ui5o+IvcABA2Ds60stWf0YODRp3/", null);
		//FileTracker ft= Peer.fileTrackers.get("yrSMsZyfIalXoHb19h915t6Ntk/Q7R4e");
		
		
		/*
		FileTracker ft = new FileTracker("README.md", 19, 4, "s9j8Ui5o+IvcABA2Ds60stWf0YODRp3/", ".");
		
		PeerConnection conn = new PeerConnection("127.0.0.1", 3001,ft);
		
		List<Integer> pieceOffsets = new ArrayList<>();
		pieceOffsets.add(new Integer(0));
		pieceOffsets.add(new Integer(1));
		pieceOffsets.add(new Integer(2));
		pieceOffsets.add(new Integer(3));
		pieceOffsets.add(new Integer(4));


		Map<Integer, byte[]> pieces = conn.getpieces(pieceOffsets, ft); // last peice is not 4 in lenght ??
		
		Storage.writePiece("./output", pieces.get(0), 0 * ft.getPieceSize());
		Storage.writePiece("./output", pieces.get(1), 1 * ft.getPieceSize());
		Storage.writePiece("./output", pieces.get(2), 2 * ft.getPieceSize());
		Storage.writePiece("./output", pieces.get(3), 3 * ft.getPieceSize());
		Storage.writePiece("./output", pieces.get(4), 4 * ft.getPieceSize());
		
	
		
		
		*/
	}

}
