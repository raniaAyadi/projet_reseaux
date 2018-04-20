import java.util.HashMap;
import java.util.Map;

public class App {


	
	public static void main(String[] args) throws Exception {
		
		ApplicationContext ctx = new ApplicationContext( args);
		
		// to test file downlaoder
		UserAction.startLeech("deathnote.mp3", 2911821, 16384, "/PevwjqG5je/3t+thYnOCGcav/avCPzs", null);

		// TODO: start up interface listener

	}

}
