package peer.server;

import java.util.Set;

import peer.ApplicationContext;
import peer.Constant;
import peer.storage.FileTracker;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Map;

public class GetResponseServer extends Response {
	private static final String SEP = " ";
	private static final String DATA ="data";
	

	public GetResponseServer(OutputStream out,Map<String, Object> fields) throws ProtocolException, IOException, PieceNotAvailableException {
		super(out, fields);
	}

	@Override
	protected void verify() throws ProtocolException, PieceNotAvailableException {
		String key = (String) fields.get(Constant.Config.KEY);
		@SuppressWarnings("unchecked")
		Set<Integer> parts = (Set<Integer>)fields.get(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);

		if(ApplicationContext.fileTrackers.containsKey(key) == false){
			throw new ProtocolException("Vérfier la clé "+key);
		}
		else {
			FileTracker f = ApplicationContext.fileTrackers.get(key);
			String bufferMap = f.getBuffermap();
	
			for(Integer i : parts) {
				try {
					if(bufferMap.charAt(i) == '0') {
						throw new PieceNotAvailableException("La partie "+i+" n'est pas disponible");
					}
				}
				catch(IndexOutOfBoundsException ex) {
					ex.printStackTrace();
					throw new ProtocolException("La partie "+i+" n'existe pas !");
				}
			}
		}
	}		

	@Override
	protected void sendMessage(InetAddress add) throws IOException {
		String key = (String) fields.get(Constant.Config.KEY);
		FileTracker f = ApplicationContext.fileTrackers.get(key);
		
		@SuppressWarnings("unchecked")
		Set<Integer> parts = (Set<Integer>)fields.get(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);
		
		String initMessage = DATA+SEP+key+SEP+"[";
		out.write(initMessage.getBytes());
		out.flush();
		
		int j = 0;
		for(Integer i : parts) {
			j++;
			String s = i.toString()+":";
			out.write(s.getBytes());
			
		    try {
				out.write(f.getPiece(i));
				System.out.println("size "+f.getPiece(i).length);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		    
		    if(j<parts.size())
		    	out.write(SEP.getBytes());
		    System.out.println("im here");
			out.flush();
			this.log.fine("Piece "+i+" of file "+f.getFileName()+" is uploaded by "+add.toString());
		}
		
		out.write("]".getBytes());
		out.flush();
		
	}
	
}
