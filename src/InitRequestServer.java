import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * Classe RequestInitialisation
 * permet de décoder et de vérifier la requete d'initialisation envoyé par le client
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
