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
		
		if(config.getKey(key) == false){
			throw new ProtocolException("Vérfier la clé "+key);
		}
		
		String bufferFile = this.config.getField(key, Constant.Config.BUFFER_MAP);
		if(bufferFile.length() != bufferMap.length()) {
			throw new ProtocolException("Vérifier le buffer Map "+bufferMap);
		}
	}

	@Override
	protected void setMessage() throws IOException {
		String key = (String) this.fields.get(Constant.Config.KEY);
		String bufferMap = config.getField(key, Constant.Config.BUFFER_MAP);
		
		this.message = HAVE + SEP + key + SEP + bufferMap;
	}

}
