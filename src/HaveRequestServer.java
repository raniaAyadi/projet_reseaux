
public class HaveRequestServer extends Request {
	private static final String REG = "have\\p{Space}+(\\p{Graph}+)\\p{Space}*"
									+ "([01]+)\\p{Space}*";

	public HaveRequestServer(String in) throws ProtocolException {
		super(in);
	}

	@Override
	protected void setExp() {
		this.exp = REG;
	}

	@Override
	protected void putFields() {
		String key = this.matcher.group(1);
		String bufferMap = this.matcher.group(2);
		
		this.fields.put(Constant.Config.KEY, key);
		this.fields.put(Constant.Config.BUFFER_MAP, bufferMap);

	}

}
