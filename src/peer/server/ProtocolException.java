package peer.server;


/**
 * Class ProtocolException
 * une exception est d�clench�e si une requete recue n''est pas conforme au protocole associ�
 */

@SuppressWarnings("serial")
public class ProtocolException extends Exception {
	
	/**
	 * 
	 * @param s message � afficher
	 */
	public ProtocolException(String s) {
		super(s);
	}
}
