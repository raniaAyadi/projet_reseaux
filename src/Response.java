import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

abstract public class Response {
	protected Map<String, Object> fields;   // input
	protected PrintWriter out;
	
	public Response(PrintWriter out, Map<String, Object> fields) throws ProtocolException, IOException, PieceNotAvailableException {
		this.fields = fields;
		this.out = out;
		
		verify();		
	}
	
	protected abstract void verify() throws ProtocolException, PieceNotAvailableException;
	
	protected abstract void sendMessage() throws IOException;
}
