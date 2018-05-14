package peer.userInterface;


public class FileNotAvailableException extends Exception {
	public FileNotAvailableException() {
		super();
	}
	
	public FileNotAvailableException(String msg) {
		super(msg);
	}
}
