import java.util.Set;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class GetResponseServer extends Response {
	private static final String SEP = " ";
	private static final String DATA ="data";
	

	public GetResponseServer(PrintWriter out,Map<String, Object> fields) throws ProtocolException, IOException, PieceNotAvailableException {
		super(out, fields);
		this.out = out;
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
					if(bufferMap.charAt(i-1) == '0') {
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
	protected void sendMessage() throws IOException {
		String key = (String) fields.get(Constant.Config.KEY);
		FileTracker f = ApplicationContext.fileTrackers.get(key);
		
		@SuppressWarnings("unchecked")
		Set<Integer> parts = (Set<Integer>)fields.get(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);
		
		String initMessage = DATA+SEP+key+SEP+"[";
		out.print(initMessage);
		
		int j = 0;
		for(Integer i : parts) {
			j++;
			String s = i.toString()+":";
			out.print(s);
		    try {
				out.print(f.getPiece(i));
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
		    
		    if(j<parts.size())
		    	out.print(SEP);
		}
		
		out.print("]");
		out.flush();
	}
	
}
