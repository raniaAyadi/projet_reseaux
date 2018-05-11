
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

abstract public class Response {
	protected Map<String, Object> fields;   // input
	protected OutputStream out;
	
	public Response(OutputStream out, Map<String, Object> fields) throws ProtocolException, IOException, PieceNotAvailableException {
		this.fields = fields;
		this.out = out;
		
		verify();		
	}
	
	protected abstract void verify() throws ProtocolException, PieceNotAvailableException;
	
	protected abstract void sendMessage() throws IOException;
}
