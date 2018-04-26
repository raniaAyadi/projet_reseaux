public class App {


	
	public static void main(String[] args) throws Exception {
		
		@SuppressWarnings("unused")
		ApplicationContext ctx = new ApplicationContext( args);

		//int id = UserAction.startLeech("class_diagram.gif", 30945, 64, "Tux1uESq4hj+w298b3eAvULVBJazfNud", null);
		UserAction.startLeech("Rapport_r_seaux.pdf", 321700, 64, "Ju8Sby7B+qiYSeX4OLEWmnEs21Sqd4dh", null);
		//Thread.sleep(3000);
		//ApplicationContext.getById(id).setDownSpeed(1024);
		
		
		// TODO: start up interface listener

	}

}
