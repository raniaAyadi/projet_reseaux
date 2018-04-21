/**
 * Classe RequestInitialisation
 * permet de d�coder et de v�rifier la requete d'initialisation envoy� par le client
 */

public class InitRequestServer extends Request{

	private static final String REG = "[iI][nN][tT][eE][rR][eE][sS][tT][eE][dD]"
									+ "\\p{Space}+(\\p{Alnum}+)\\p{Space}*";
	
	/**
	 * 
	 * @param in le message recu 
	 * @throws ProtocolException si le message n'est pas conforme au protocole
	 */
	public InitRequestServer(String in) throws ProtocolException {
		super(in);
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
