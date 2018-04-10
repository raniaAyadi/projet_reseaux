import java.util.Set;
import java.io.IOException;
import java.util.Map;

public class GetResponseServer {
	private String key;
	private Set<Integer> parts;
	private String message;                // to send
	private Config config;
	private static final String DATA = "data";
	private static final String SEP =" ";
	
	public GetResponseServer(GetRequestServer req) throws ProtocolException {
		this.key = (String)req.getField(Constant.Config.KEY);
		this.parts = (Set<Integer>)req.getField(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);
		this.config = Config.getInstance();
		verify();
		this.message = null;
	}
	
	private void setMessage() throws IOException {
		Map<String, String> fileProp = (Map<String, String>) config.getField(key);
		String[] buff = new String[parts.size()];
		String filePath = fileProp.get(Constant.Config.FILE_NAME);
		Set<String> set = fileProp.keySet();
		System.out.println("setMessage");
		for(String s:set) {
			System.out.println(s+"  "+fileProp.get(s));
		}
		int cutSize = Integer.parseInt(fileProp.get(Constant.Config.CUT));
		
		int j=0;
		for(Integer i : parts) {
			String s = i.toString()+":"+Operation.readPart(filePath, cutSize, i);
			buff[j] = s;
			j++;
		}
		
		String s = String.join(SEP, buff);
		System.out.println("s is "+s);
		message = DATA+SEP+key+SEP+"["+s+"]";
	}
	
	public String getMessage() throws IOException {
		if (message == null) {
			setMessage();
		}
		return message;
	}
	
	private void verify() throws ProtocolException {
		if(config.getKey(this.key) == false){
			throw new ProtocolException("Vérfier la clé "+this.key);
		}
		else {
			Map<String, String> m = (Map<String, String>)config.getField(this.key);
			String bufferMap = m.get(Constant.Config.BUFFER_MAP);
			for(Integer i : this.parts) {
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
}
