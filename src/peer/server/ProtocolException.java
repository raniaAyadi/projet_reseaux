package peer.server;


/**
 * Class ProtocolException
 * une exception est declenchee si une requete recue n est pas conforme au protocole associe
 */

@SuppressWarnings("serial")
public class ProtocolException extends Exception {
	
	/**
	 * 
	 * @param s message a afficher
	 */
	public ProtocolException(String s) {
		super(s);
	}
}
