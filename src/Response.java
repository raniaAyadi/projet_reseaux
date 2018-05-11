
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.logging.Logger;

abstract public class Response {
	protected Map<String, Object> fields;   // input
	protected OutputStream out;
	protected Logger log;
	
	public Response(OutputStream out, Map<String, Object> fields) throws ProtocolException, IOException, PieceNotAvailableException {
		this.fields = fields;
		this.out = out;
		this.log = Logger.getLogger(Constant.Log.UPLOAD_LOG);
		
		verify();		
	}
	
	protected abstract void verify() throws ProtocolException, PieceNotAvailableException;
	
	protected abstract void sendMessage(InetAddress add) throws IOException;
}
