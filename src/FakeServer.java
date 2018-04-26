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

/**
 * Fake server used just to test fileDownloader (until Server is integrated)
 * @author Hmama Adem
 *
 */
public class FakeServer {
	public static void start(Map<String, FileTracker> fileTrackers,int port) throws Exception{
		
		
		System.out.println("listening on port : " + port);
		ServerSocket s = new ServerSocket(port);
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
						pred.write("have " + requestedKey + " ");
						FileTracker resp = fileTrackers.get(requestedKey);
						pred.write(resp.getBuffermap());
						pred.flush();
						soc.close();
						continue;
					}else{
						res = "i dont serve this shit";
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
							System.out.println("peer requested an invalid index : " + pieceIndex);
							System.out.println("returing a string of spaces");
						}
						byte[] piece;
						synchronized (fileTrackers.get(key)) {
							 piece= fileTrackers.get(key).getPiece(pieceIndex);
						}
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
