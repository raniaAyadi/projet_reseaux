import java.io.IOException;
import java.util.Map;

public class HaveResponseServer extends Response {
	
	private static final String HAVE = "have";
	private static final String SEP =" ";

	public HaveResponseServer(Map<String, Object> fields) throws ProtocolException, IOException {
		super(fields);
	}

	@Override
	protected void verify() throws ProtocolException {
		String key = (String) this.fields.get(Constant.Config.KEY);
		String bufferMap = (String) this.fields.get(Constant.Config.BUFFER_MAP);
		Map<String, FileTracker> l = ApplicationContext.fileTrackers;
		
		if(l.containsKey(key) == false){
			throw new ProtocolException("V�rfier la cl� "+key);
		}
		
		FileTracker m = l.get(key);
		String bufferFile = Operation.bitsetToString(m.getBufferMap());
		if(bufferFile.length() != bufferMap.length()) {
			throw new ProtocolException("V�rifier le buffer Map "+bufferMap);
		}
	}

	@Override
	protected void setMessage() throws IOException {
		String key = (String) this.fields.get(Constant.Config.KEY);
		String bufferMap = Operation.bitsetToString(ApplicationContext.fileTrackers.get(key).getBufferMap());
		
		this.message = HAVE + SEP + key + SEP + bufferMap;
	}

}
