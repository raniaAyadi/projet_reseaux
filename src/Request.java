import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe Decoder
 * Decoder est une classe abstraite pour d�coder un message
 */

public abstract class Request {
	
	protected String in;            	   // le message � d�coder
	protected Map<String, Object> fields;  // dictionnaire des champs
	protected Pattern reg;                 // l'expression r�guli�re
	protected Matcher matcher;             // pour analyser le message
	protected String exp;                  // la chaine qui d�crit reg                  
	/**
	 * 
	 * @param in le message � d�coder 
	 * @throws ProtocolException 
	 */
	public Request(String in) throws ProtocolException  {
		this.in = in;
		setExp();
		this.init();
	}
	
	private void init()throws ProtocolException {
		this.reg = Pattern.compile(exp);
		this.matcher = this.reg.matcher(this.in);
		verify();
		setFields();
	}
	
	/**
	 * C'est la classe fille, concr�te, qui d�finit l'expression r�guli�re
	 */
	protected abstract void setExp();
	
	private final void setFields(){
		fields = new HashMap<String, Object>();
		putFields();
	}
	
	/**
	 * Lire un champ identifi�e par son nom 
	 * @param key le nom de la propri�t� � lire
	 * @return value = null si le cl� n'existe pas
	 */
	public Object getField(String key) {
		return this.fields.get(key);
	}
	
	/**
	 * m�thode abstraite
	 * remplir le dictionanire fields
	 */
	protected abstract void putFields();
	
	/**
	 * V�rfiier si le message est conforme au protocole, sinon une exception est lev�e
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
