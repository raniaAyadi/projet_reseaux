import java.io.File;
import java.util.Map;

/**
 * Classe InitResponse
 * vérifie la validité de la requete, passée en paramètre, et construit le message à envoyer
 */
public class InitResponseServer {
	private String key;
	private String bufferMap;
	private String message;                    // to send
	private Config config;
	public static final String HAVE = "have";
	public static final String SEP = " ";
	
	/**
	 * Instanciation et vérififcation de la requete (existence du fichier identifié par sa clé)
	 * @param req
	 * @throws ProtocolException
	 */
	public InitResponseServer(InitRequestServer req) throws ProtocolException {
		this.key = (String)req.getField(Constant.Config.KEY);
		this.config = Config.getInstance();

		verify();
		setBufferMap();
		this.message = null;
	}
	
	private void setBufferMap() {
		Map<String, String> m = (Map<String,String>)config.getField(key);
		this.bufferMap = m.get(Constant.Config.BUFFER_MAP);
	}
	
	private void setMessage() {
		String[] out = new String[3];
		out[0] = HAVE;
		out[1] = this.key;
		out[2] = this.bufferMap;
		this.message = String.join(SEP, out);
	}
	
	/**
	 * @return le message à envoyer
	 */
	public String getMessage() {
		if(message == null) {
			setMessage();
		}
		return message;
	}
	
	private void verify() throws ProtocolException {
		if(config.getKey(this.key) == false){
			throw new ProtocolException("Vérfier la clé "+this.key);
		}
	}

}
