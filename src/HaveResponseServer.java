
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Map;

public class HaveResponseServer extends Response {
	
	private static final String HAVE = "have";
	private static final String SEP =" ";

	public HaveResponseServer(OutputStream out, Map<String, Object> fields) throws ProtocolException, IOException, PieceNotAvailableException {
		super(out, fields);
	}

	@Override
	protected void verify() throws ProtocolException {
		String key = (String) this.fields.get(Constant.Config.KEY);
		String bufferMap = (String) this.fields.get(Constant.Config.BUFFER_MAP);
		
		if(ApplicationContext.fileTrackers.containsKey(key) == false){
			throw new ProtocolException("Vérfier la clé "+key);
		}
		
		FileTracker f = ApplicationContext.fileTrackers.get(key);
		String bufferFile = f.getBuffermap();
		if(bufferFile.length() != bufferMap.length()) {
			throw new ProtocolException("Vérifier le buffer Map "+bufferMap);
		}
		
	}

	@Override
	protected void sendMessage(InetAddress add) throws IOException {
		String key = (String) this.fields.get(Constant.Config.KEY);
		FileTracker f = ApplicationContext.fileTrackers.get(key);
		String bufferMap = f.getBuffermap();
		
		String message = HAVE + SEP + key + SEP + bufferMap;
		PrintWriter p = new PrintWriter(out);
		
		p.print(message);
		p.flush();
	}

}
