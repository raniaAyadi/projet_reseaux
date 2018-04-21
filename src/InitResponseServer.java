import java.io.IOException;
import java.util.Map;

/**
 * Classe InitResponse
 * v�rifie la validit� de la requete, pass�e en param�tre, et construit le message � envoyer
 */
public class InitResponseServer extends Response {
	public static final String HAVE = "have";
	public static final String SEP = " ";	

	
	public InitResponseServer(Map<String, Object> fields) throws ProtocolException, IOException {
		super(fields);
	}
	
	@Override
	protected void verify() throws ProtocolException {
		String key = (String) this.fields.get(Constant.Config.KEY);
		if(ApplicationContext.fileTrackers.containsKey(key) == false){
			throw new ProtocolException("V�rfier la cl� "+key);
		}		
	}
	@Override
	protected void setMessage() {
		String key = (String) this.fields.get(Constant.Config.KEY);
		FileTracker m = ApplicationContext.fileTrackers.get(key);
		String bufferMap = Operation.bitsetToString(m.getBufferMap());
		
		String[] out = new String[3];
		out[0] = HAVE;
		out[1] = key;
		out[2] = bufferMap;
		this.message = String.join(SEP, out);
	}
	

}
