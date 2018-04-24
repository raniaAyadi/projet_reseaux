public class App {


	
	public static void main(String[] args) throws Exception {
		
		@SuppressWarnings("unused")
		ApplicationContext ctx = new ApplicationContext( args);
		
		// to test file downlaoder
		UserAction.startLeech("Big_data.pdf", 3186700, 16384, "m+My/DmC2DJm1M2X5wFUsJ8IbaSXsgL2", null);

		// TODO: start up interface listener

	}

}
