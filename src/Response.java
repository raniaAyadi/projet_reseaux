import java.io.IOException;
import java.util.Map;

abstract public class Response {
	protected Map<String, Object> fields;   // input
	protected String message; 			  // output
	
	public Response(Map<String, Object> fields) throws ProtocolException, IOException {
		this.fields = fields;
		
		verify();		
		setMessage();
	}
	
	public String getMessage() {
		return message;
	}
	
	protected abstract void verify() throws ProtocolException;
	
	public String updateMessage(Map<String, Object> m) throws ProtocolException, IOException {
		this.fields = m;
		this.verify();
		this.setMessage();
		return message;
	}
	
	protected abstract void setMessage() throws IOException;
}
