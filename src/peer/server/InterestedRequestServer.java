package peer.server;
import java.net.InetAddress;

import peer.Constant;

/**
 * Classe RequestInitialisation
 * permet de décoder et de vérifier la requete d'initialisation envoyé par le client
 */

public class InterestedRequestServer extends Request{

	private static final String REG = "interested\\p{Space}+(\\p{Graph}+)\\p{Space}*";

	
	/**
	 * 
	 * @param in le message recu 
	 * @throws ProtocolException si le message n'est pas conforme au protocole
	 */
	public InterestedRequestServer(String in, InetAddress address) throws ProtocolException {
		super(in, address);
	}	

	@Override
	protected void setExp() {
		this.exp = REG;
	}

	@Override
	protected void putFields() {
		String key = this.matcher.group(1);
		this.fields.put(Constant.Config.KEY, key);
		
	}

	
}
