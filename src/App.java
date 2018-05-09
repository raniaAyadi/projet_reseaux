public class App {


	
	public static void main(String[] args) throws Exception {
		
		@SuppressWarnings("unused")
		ApplicationContext ctx = new ApplicationContext( args);
		UserAction.download("error.png", null, null, null);
		UserAction.download("dot.png", null, null, null);
		//int id = UserAction.startSeed("C:\\Users\\msi\\workspace\\test-project\\adem.txt");
		//int id = UserAction.startLeech("class_diagram.gif", 30945, 64, "yzw9NtHFBuTlVQwFP/qO2dBIhS4ymERf", null);
		
		//Thread.sleep(2000);
		//UserAction.removeFile(id);
		//System.out.println("done");
		
		
		//int id = UserAction.startLeech("video.mp4", 21477588, 65536, "ktuY7RlOVhNQdTBmnjk5hh0nWy2qvoWa", null);
		//UserAction.startLeech("rapport.pdf", 403493, 64, "imPcvB0b0hWtFz6bnSXyTxZruXL+D4mA", null);
		//UserAction.startLeech("prog.exe", 1853389, 64, "8ZmsamaUG+eItfRYuwHoG/UrzRzQmLtz", null);
		//UserAction.startLeech("class_diagram.gif", 30945, 64, "yzw9NtHFBuTlVQwFP/qO2dBIhS4ymERf", null);
		
		//Thread.sleep(3000);
		//ApplicationContext.getById(id).setDownSpeed(1024);
		// TODO: start up interface listener

	}

}
