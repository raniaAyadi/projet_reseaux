package peer.distribution;
import java.util.Map;
import java.util.logging.Logger;

import peer.Config;
import peer.server.ProtocolException;

public class NeighbourdResponse{
	private  Map<String, Object> fields;   // input
	@SuppressWarnings("unused")
	private Logger log;
	private String message;
	
	public NeighbourdResponse(Map<String, Object> fields) throws ProtocolException{
		this.fields = fields;
		this.log = Logger.getLogger(this.getClass().getName());
		
		verify();		
	}

	protected void verify() throws ProtocolException {
		String applicationName = (String)fields.get("applicationName");
		Integer version = (Integer)fields.get("version");
		
		if(! applicationName.equals(Config.applicationName))
			throw new ProtocolException("Verfifier le nom de application");
		
		if(! version.equals(Config.applicationVersion))
			throw new ProtocolException("Verfifier la version de application");

	}

	protected void setMessage() {
		String applicationName = (String)fields.get("applicationName");
		Integer version = (Integer)fields.get("version");
		
		this.message = "ok "+"\""+applicationName+"\" "+version+" "+Config.listenPort;
	}

	public String getMessage() {
		return this.message+"\n";
	}

}
