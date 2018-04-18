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
		Set<Integer> parts = (Set<Integer>)fields.get(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);

		if(config.getKey(key) == false){
			throw new ProtocolException("Vérfier la clé "+key);
		}
		else {
			Map<String, String> m = (Map<String, String>)config.getField(key);
			String bufferMap = m.get(Constant.Config.BUFFER_MAP);
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
		Map<String, String> fileProp = (Map<String, String>) config.getField(key);
		Set<Integer> parts = (Set<Integer>)fields.get(Constant.InitResponseServer.PARTS_TO_DOWNLOAD);

		String[] buff = new String[parts.size()];
		String filePath = fileProp.get(Constant.Config.FILE_NAME);
		Set<String> set = fileProp.keySet();
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
	
}
