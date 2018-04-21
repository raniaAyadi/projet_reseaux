import java.util.HashSet;
import java.util.Set;

public class GetRequestServer extends Request {
	
	private static final String REG = "[gE][eE][tT][pP][iI][eE][cC][eE][sS]\\p{Space}+(\\p{Alnum}+)\\p{Space}+"
									+ "\\[([0-9]+(\\p{Space}[0-9]+)*)\\]\\p{Space}*";
	
	
	public GetRequestServer(String in) throws ProtocolException {
		super(in);
	}

	@Override
	protected void setExp() {
		this.exp = REG;
	}

	@Override
	protected void putFields() {
		Set<Integer> parts = new HashSet<>();
		this.fields.put(Constant.Config.KEY, this.matcher.group(1));
		
		String buff[] = this.matcher.group(2).split("\\p{Space}");
		int n = buff.length;
		for(int i=0; i<n; i++) {
			parts.add(Integer.parseInt(buff[i]));
		}
		this.fields.put(Constant.InitResponseServer.PARTS_TO_DOWNLOAD, parts);
		
	}



}
