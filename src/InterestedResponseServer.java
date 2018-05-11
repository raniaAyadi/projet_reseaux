
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Classe InitResponse
 * vérifie la validité de la requete, passée en paramètre, et construit le message à envoyer
 */
public class InterestedResponseServer extends Response {
	public static final String HAVE = "have";
	public static final String SEP = " ";	

	
	public InterestedResponseServer(OutputStream out, Map<String, Object> fields) throws ProtocolException, IOException, PieceNotAvailableException {
		super(out, fields);
	}
	
	@Override
	protected void verify() throws ProtocolException {
		String key = (String) this.fields.get(Constant.Config.KEY);
		System.out.println(this.getClass().getName()+key);
		if(ApplicationContext.fileTrackers.containsKey(key) == false){
			throw new ProtocolException("Vérfier la clé "+key);
		}		
	}
	@Override
	protected void sendMessage() {
		String key = (String) this.fields.get(Constant.Config.KEY);
		FileTracker f = ApplicationContext.fileTrackers.get(key);
		String bufferMap = f.getBuffermap();
		
		String[] out = new String[3];
		out[0] = HAVE;
		out[1] = key;
		out[2] = bufferMap;
		String message = String.join(SEP, out);
		
		PrintWriter p = new PrintWriter(this.out);
		p.print(message);
		p.flush();
	}
	

}
