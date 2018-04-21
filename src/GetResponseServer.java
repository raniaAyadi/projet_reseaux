import java.util.Set;
import java.io.IOException;
import java.util.Map;

public class GetResponseServer extends Response {
	private static final String SEP = " ";
	private static final String DATA ="data";

	public GetResponseServer(Map<String, Object> fields) throws ProtocolException, IOException {
		super(fields);
	}

	@Override
	protected void verify() throws ProtocolException {
		String key = (String) fields.get(Constant.Config.KEY);
		@SuppressWarnings("unchecked")
		Set<Integer> parts = (Set<Integer>)fields.get(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);
		Map<String, FileTracker> l = ApplicationContext.fileTrackers;
		
		if(l.containsKey(key) == false){
			throw new ProtocolException("V�rfier la cl� "+key);
		}
		else {
			FileTracker m = l.get(key);
			String bufferMap = Operation.bitsetToString(m.getBufferMap());
			for(Integer i : parts) {
				try {
					if(bufferMap.charAt(i-1) == '0') {
						throw new ProtocolException("La partie "+i+" n'est pas disponible");
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
	protected void setMessage() throws IOException {
		String key = (String) fields.get(Constant.Config.KEY);
		Map<String, FileTracker> l = ApplicationContext.fileTrackers;
		FileTracker m = l.get(key);
		@SuppressWarnings("unchecked")
		Set<Integer> parts = (Set<Integer>)fields.get(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);

		String[] buff = new String[parts.size()];
		
		int j=0;
		for(Integer i : parts) {
			String s = null;
			try {
				s = i.toString()+":"+m.getPiece(i);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			buff[j] = s;
			j++;
		}
		
		String s = String.join(SEP, buff);
		System.out.println("s is "+s);
		message = DATA+SEP+key+SEP+"["+s+"]";		
	}
	
}
