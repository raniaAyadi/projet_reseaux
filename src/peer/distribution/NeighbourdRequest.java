package peer.distribution;

import peer.server.ProtocolException;
import peer.server.Request;

public class NeighbourdRequest extends Request{

	private static final String EXP = "neighbourhood \"(\\p{Graph}+)\" ([0-9]+)";
	
	public NeighbourdRequest(String in) throws ProtocolException {
		super(in);
	}

	@Override
	protected void setExp() {
		this.exp = EXP;
	}

	@Override
	protected void putFields() {
		String applicationName;
		Integer version;
		
		applicationName = this.matcher.group(1);
		version = Integer.parseInt(this.matcher.group(2));
		
		this.fields.put("applicationName", applicationName);
		this.fields.put("version", version);
	}
	
}
