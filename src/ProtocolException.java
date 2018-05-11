

/**
 * Class ProtocolException
 * une exception est d�clench�e si une requete recue n''est pas conforme au protocole associ�
 */

public class ProtocolException extends Exception {
	
	/**
	 * 
	 * @param s message � afficher
	 */
	public ProtocolException(String s) {
		super(s);
	}
}
