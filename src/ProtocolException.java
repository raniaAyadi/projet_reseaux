
/**
 * Class ProtocolException
 * une exception est déclenchée si une requete recue n''est pas conforme au protocole associé
 */

public class ProtocolException extends Exception {
	
	/**
	 * 
	 * @param s message à afficher
	 */
	public ProtocolException(String s) {
		super(s);
	}
}
