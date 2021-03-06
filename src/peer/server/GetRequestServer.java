package peer.server;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import peer.Constant;

public class GetRequestServer extends Request {
	
	private static final String REG = "getpieces\\p{Space}+(\\p{Graph}+)\\p{Space}+"
									+ "\\[([0-9]+(\\p{Space}[0-9]+)*)\\]\\p{Space}*";
	
	
	public GetRequestServer(String in, InetAddress address) throws ProtocolException {
		super(in, address);
	}

	@Override
	protected void setExp() {
		this.exp = REG;
	}

	@Override
	protected void putFields() {
		@SuppressWarnings("unused")
		String key;
		Set<Integer> parts = new HashSet<>();
		
		key = this.matcher.group(1);
		this.fields.put(Constant.Config.KEY, this.matcher.group(1));
		
		String buff[] = this.matcher.group(2).split("\\p{Space}");
		int n = buff.length;
		for(int i=0; i<n; i++) {
			parts.add(Integer.parseInt(buff[i]));
		}
		this.fields.put(Constant.InitResponseServer.PARTS_TO_DOWNLOAD, parts);
		
	}



}
