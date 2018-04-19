
public class TestConfigClass {

	public static void main(String[] args) throws Exception{
		// Just init it and use it, to ease testing generate executable jar file from this main
		MyConfig.init(args);
		System.out.println("listen-port: " + MyConfig.listenPort);
		System.out.println("trackerPort:" + MyConfig.trackerPort);
		System.out.println("trackerIp:" + MyConfig.trackerIp);
		System.out.println("download path:" + MyConfig.downloadPath);
		System.out.println("upload path:" + MyConfig.uploadPath);
		System.out.println("meta path:" + MyConfig.metaPath);
	}
}
