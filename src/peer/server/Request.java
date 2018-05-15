package peer.server;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import peer.Config;

/**
 * Classe Decoder
 * Decoder est une classe abstraite pour decoder un message
 */

public abstract class Request {
	
	protected String in;            	   // le message à décoder
	protected Map<String, Object> fields;  // dictionnaire des champs
	protected Pattern reg;                 // l'expression régulière
	protected Matcher matcher;             // pour analyser le message
	protected String exp;                  // la chaine qui décrit reg    
	protected InetAddress address;
	protected Logger log;
	/**
	 * 
	 * @param in le message à décoder 
	 * @throws ProtocolException 
	 */
	public Request(String in, InetAddress address) throws ProtocolException  {
		this.in = in;
		this.address = address;
		setExp();
		
		this.log = Config.uploadLog;
		this.log.config("The Peer "+address.getAddress().toString()+" send : "+in);

		this.init();
		}
	
	public Request(String in) throws ProtocolException  {
		this.in = in;
		setExp();
		this.log = Logger.getLogger(this.getClass().getName());

		this.init();
	}
	
	private void init()throws ProtocolException {
		this.reg = Pattern.compile(exp);
		this.matcher = this.reg.matcher(this.in);
		verify();
		setFields();
	}
	
	/**
	 * C'est la classe fille, concrète, qui définit l'expression régulière
	 */
	protected abstract void setExp();
	
	private final void setFields(){
		fields = new HashMap<String, Object>();
		putFields();
	}
	
	/**
	 * Lire un champ identifiée par son nom 
	 * @param key le nom de la propriété à lire
	 * @return value = null si le clé n'existe pas
	 */
	public Object getField(String key) {
		return this.fields.get(key);
	}
	
	/**
	 * methode abstraite
	 * remplir le dictionanire fields
	 */
	protected abstract void putFields();
	
	/**
	 * Verfiier si le message est conforme au protocole, sinon une exception est levée
	 * @throws ProtocolException
	 */
	private void verify() throws ProtocolException {
		if(matcher.matches() == false) {
			throw new ProtocolException("Verify your message "+this.getClass().getName());
		}
	}
	
	public void setIn(String in) throws ProtocolException {
		this.in = in;
		init();
	}
	
	public Map<String, Object> getFields(){
		return this.fields;
	}
	
}
