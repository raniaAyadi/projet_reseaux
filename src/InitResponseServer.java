import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Classe InitResponse
 * vérifie la validité de la requete, passée en paramètre, et construit le message à envoyer
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
		if(config.getKey(key) == false){
			throw new ProtocolException("Vérfier la clé "+key);
		}		
	}
	@Override
	protected void setMessage() {
		String key = (String) this.fields.get(Constant.Config.KEY);
		Map<String, String > m = (Map<String, String>) config.getField(key);
		String bufferMap = m.get(Constant.Config.BUFFER_MAP);
		
		String[] out = new String[3];
		out[0] = HAVE;
		out[1] = key;
		out[2] = bufferMap;
		this.message = String.join(SEP, out);
	}
	

}
