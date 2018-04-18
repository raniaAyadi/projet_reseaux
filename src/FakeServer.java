import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.BitSet;
import java.util.Map;

public class FakeServer {
	public static void start(Map<String, FileTracker> fileTrackers) throws Exception{
		// fake file server
		ServerSocket s = new ServerSocket(3001);

		while (true) {
			Socket soc = s.accept();
			System.out.println("client connected");

			BufferedReader plec = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			OutputStream os = soc.getOutputStream();
			PrintWriter pred = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)), true);

			
			String request = plec.readLine();
			String res= new String("");
			String[] req = request.split(" ");
			if(req[0].equals("interested")){
				if(req.length != 2){
					res = "only two arguments in this type of request";
				}else{
					String requestedKey = req[1];
					if(fileTrackers.containsKey(requestedKey)){
						// write directly
						pred.write("have " + requestedKey + " ");
						
						
						// write the buffermap
						FileTracker resp = fileTrackers.get(requestedKey);
						BitSet buffermap = resp.getBufferMap();
						pred.write(Operation.bitsetToString(buffermap, resp.getNumberPieces()));
						pred.flush();
						soc.close();
						continue;
					}else{
						res = "i dont serve this file";
					}
				}
			}else if(req[0].equals("getpieces")){
				String key = req[1];
				if(!fileTrackers.containsKey(key)){
					pred.write("i dont have this key man");
					pred.flush();
					soc.close();
					continue;
				}
				String indexes = request.substring(request.indexOf('['));
				if(indexes.length() == 2){
					res = "data " + key + "[]";
				}else{
					String[] ls = indexes.substring(1, indexes.length()-1).split(" ");
					pred.write("data " + key+" [");
					pred.flush();
					for(int i =0;i< ls.length; i++){
						pred.write(ls[i] + ":" );
						pred.flush();
						int pieceIndex = Integer.parseInt(ls[i]);
						if(pieceIndex >= fileTrackers.get(key).getSize()){
							// invalid index requested 
							System.out.println("peer requested an invalid index : " + pieceIndex);
							System.out.println("returing a string of spaces");
						}
						byte[] piece = fileTrackers.get(key).getPiece(pieceIndex);
						os.write(piece);
						if(i != ls.length-1){
							pred.write(" ");
							pred.flush();
						}	
					}
					pred.write("]");
					pred.flush();
					soc.close();
					continue;
				}
			}else{
				res = "unregonized command";
			}
		
			

			pred.write(res);
			pred.flush();
			soc.close();
		}
	}
}
