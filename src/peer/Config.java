package peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 
 * In order for the application to run, a list of configuration global variables
 * must be set in this class, which is based on two things: command line
 * arguments and configuration file values, if some critical values like tracker
 * address are not resolved, or in case of invalid command line arguments, the
 * init method should display descriptive error message and force shut down the
 * application program.
 * 
 * @author Hmama Adem
 * 
 */
@SuppressWarnings("deprecation")
public class Config {

	public static int listenPort;
	public static int trackerPort;
	public static String trackerIp;
	public static int udpPort;
	public static int uiPort;
	public static String multicastIP;
	public static int version; //centralisée = 0 et distribuée = 1
	
	public static Logger uploadLog;
	public static Logger downloadLog;
	public static Logger generalLog;
	
	public static int poolSize;
	public static int updatePeriod;
	public static int peerConnectionNumber;
	public static int ttlSearchFile;
	public static int logVerbosity;
	public static int messageMaxSize; //KB
	
	public static String applicationName;
	public static int applicationVersion;
	
	public static String metaPath;
	public static String downloadPath;
	public static String uploadPath;

	public static Properties propreties;
	private static String CONFIG_FILE_NAME = "config.properties"; // default

	/**
	 * NOTE: This might stop the application if arguments are not valid, or
	 * missing required information like tracker address
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void init(String[] args) throws Exception {

		Options options = generateOptions();
		CommandLineParser parser = new DefaultParser();
		CommandLine line = null;
		try {
			line = parser.parse(options, args);
		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("arguments are parsed from config or automatically generated if not set", options);
			System.exit(0);
		}
		if (line.hasOption("config-path")) {
			String cp = line.getOptionValue("config-path");
			File test = new File(cp);
			if (!(test.isDirectory() || !test.exists())) CONFIG_FILE_NAME = cp;
		}

		loadConfig();
		logVerbosity = 1; // default is medium = CONFIG 
		if(propreties.containsKey("log-verbosity")) {
			logVerbosity = Integer.parseInt(propreties.getProperty("log-verbosity"));
			if(logVerbosity < 0 && logVerbosity > 2) logVerbosity = 1;
		}
		setLog();
		
		
		version = 0;
		if (propreties.containsKey("version")) {
			version = Integer.parseInt(propreties.getProperty("version"));
		}
		
		boolean cont = true;
		if(version == 0) {
			if (line.hasOption("tracker-port") && line.hasOption("tracker-ip")) {
				String temp = line.getOptionValue("tracker-port");
				if (isInt(temp)) {
					trackerPort = Integer.parseInt(temp);
					trackerIp = line.getOptionValue("tracker-ip");
					cont = false;
				} else {
					generalLog.config("Invalid tracker-port argument, trying address from config...");
				}
			}

			if (cont && propreties.containsKey("tracker-port") && propreties.containsKey("tracker-ip")) {
				String temp = propreties.getProperty("tracker-port");
				if (isInt(temp)) {
					trackerPort = Integer.parseInt(temp);
					trackerIp = propreties.getProperty("tracker-ip");
					cont = false;
				} else {
					generalLog.config("Invalid tracker-port from config file");
				}
			}

			if (cont) {
				generalLog.severe("Unable to resolve tracker address");
				System.exit(-1);
			}
		}
		else {
			if (line.hasOption("udp-port") && line.hasOption("multicast-ip")) {
				String temp = line.getOptionValue("udp-port");
				if (isInt(temp)) {
					udpPort = Integer.parseInt(temp);
					multicastIP = line.getOptionValue("multicast-ip");
					cont = false;
				} else {
					generalLog.config("Invalid udp-port argument, trying address from config...");
				}
			}

			if (cont && propreties.containsKey("udp-port") && propreties.containsKey("multicast-ip")) {
				String temp = propreties.getProperty("udp-port");
				if (isInt(temp)) {
					udpPort = Integer.parseInt(temp);
					multicastIP = propreties.getProperty("multicast-ip");
					cont = false;
				} else {
					generalLog.config("Invalid udp-port from config file");
				}
			}
			if (cont) {
				generalLog.severe("Unable to resolve multicast address");
				System.exit(-1);
			}
		}

		cont = true;

		if (line.hasOption("listen-port")) {
			String temp = line.getOptionValue("listen-port");
			if (isInt(temp)) {
				listenPort = Integer.parseInt(temp);
				if (Operation.testListenPort(listenPort)) {
					cont = false;
				} else {
					generalLog.config("listen-port <" + listenPort + "> already in use");
				}
			} else {
				generalLog.config("Invalid argument: listen-port");
			}
		}

		if (cont && propreties.containsKey("listen-port")) {
			generalLog.config("Fetching listen-port from config file...");
			String temp = propreties.getProperty("listen-port");
			if (isInt(temp)) {
				listenPort = Integer.parseInt(temp);
				if (Operation.testListenPort(listenPort)) {
					cont = false;
				} else {
					generalLog.config("listen-port:  <" + listenPort + "> already in use");
				}
			} else {
				generalLog.config("Invalid listen-port specified in config file");
			}
		}

		if (cont) {
			generalLog.warning("Generating listen-port number...");
			listenPort = Operation.generateValidListenPort();
		}

		metaPath = "./.meta"; // default
		if (propreties.containsKey("meta-path")) {
			metaPath = propreties.getProperty("meta-path");
		}
		validateDirectory(metaPath); // required!

		updatePeriod = 5000;
		if (propreties.containsKey("update-period")) {
			updatePeriod = Integer.parseInt(propreties.getProperty("update-period"));
		}
		
		applicationName = "FILE_SHARE";
		if (propreties.containsKey("application-name")) {
			applicationName = propreties.getProperty("application-name");
		}
		
		applicationVersion = 0;
		if (propreties.containsKey("application-version")) {
			updatePeriod = Integer.parseInt(propreties.getProperty("application-version"));
		}
		
		poolSize = 5;
		if(propreties.containsKey("pool-size")) {
			poolSize = Integer.parseInt(propreties.getProperty("pool-size"));
		}
		
		
		peerConnectionNumber = 5;
		if(propreties.containsKey("peer-connection-number")) {
			peerConnectionNumber = Integer.parseInt(propreties.getProperty("peer-connection-number"));
		}
		
		ttlSearchFile = 20000;
		if(propreties.containsKey("ttl-search-file")) {
			ttlSearchFile = Integer.parseInt(propreties.getProperty("ttl-search-file"));
		}
		
		messageMaxSize = 1024;
		if(propreties.containsKey("message-max-size")) {
			messageMaxSize = Integer.parseInt(propreties.getProperty("message-max-size"));
		}
		
		if(propreties.containsKey("ui-port")){
			uiPort = Integer.parseInt(propreties.getProperty("ui-port"));
		}
		if(!Operation.testListenPort(uiPort)){
			uiPort = Operation.generateValidListenPort();
		}
		if(uiPort == 0) uiPort = 8080; // default
		

		uploadPath = null;
		if (propreties.containsKey("upload-path")) {
			String found = propreties.getProperty("upload-path");
			File test = new File(found);
			if (test.exists()) {
				if (test.isDirectory())
					uploadPath = found;
			} else {
				uploadPath = found;
				test.mkdir();
			}
		}
		downloadPath = null;
		if (propreties.containsKey("download-path")) {
			String found = propreties.getProperty("download-path");
			File test = new File(found);
			if (test.exists()) {
				if (test.isDirectory())
					downloadPath = found;
			} else {
				downloadPath = found;
				test.mkdir();
			}
		}
		persistPropreties();
	}

	private static void persistPropreties() throws FileNotFoundException {

		propreties.setProperty("listen-port", Integer.toString(listenPort));
		propreties.setProperty("tracker-port", Integer.toString(trackerPort));
		propreties.setProperty("tracker-ip", trackerIp);
		propreties.setProperty("meta-path", metaPath);
		propreties.setProperty("update-period", Integer.toString(updatePeriod));
		propreties.setProperty("pool-size", Integer.toString(poolSize));
		propreties.setProperty("peer-connection-number", Integer.toString(peerConnectionNumber));
		propreties.setProperty("ttl-search-file", Integer.toString(ttlSearchFile));
		propreties.setProperty("log-verbosity", Integer.toString(logVerbosity));
		propreties.setProperty("message-max-size", Integer.toString(messageMaxSize));
		propreties.setProperty("ui-port", Integer.toString(uiPort));

		if (downloadPath != null)
			propreties.setProperty("download-path", downloadPath);
		else
			propreties.remove("download-path");
		if (uploadPath != null)
			propreties.setProperty("upload-path", uploadPath);
		else
			propreties.remove("upload-path");

		File fl = new File(CONFIG_FILE_NAME);
		propreties.save(new FileOutputStream(fl), "");

	}

	private static void loadConfig() throws Exception {
		File configFile = new File(CONFIG_FILE_NAME);
		propreties = new Properties();
		if (!configFile.exists()) {
			configFile.createNewFile();
		} else {
			try {
				propreties.load(new FileInputStream(configFile));
			} catch (Exception e) {
				System.err.println("Unable to parse configuration file"); // loggers not ready yet
				System.exit(-1);
			}
		}
	}

	@SuppressWarnings("static-access")
	private static Options generateOptions() {
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("tracker-ip")
				.withDescription(
						"Ip address of tracker node, taken into consideration in case tracker-port is also specified")
				.hasArg().withArgName("String").create());
		options.addOption(OptionBuilder.withLongOpt("tracker-port")
				.withDescription("Tracker port number, taken into consideration in case tracker-ip is also specified")
				.hasArg().withArgName("Number").create());
		options.addOption(OptionBuilder.withLongOpt("listen-port")
				.withDescription("Port number on which peer will listen for file requests").hasArg()
				.withArgName("Number").create());
		options.addOption(OptionBuilder.withLongOpt("config-path")
				.withDescription(
						"Path for the configuration file, if not specified, default path(current directory) will be used")
				.hasArg().withArgName("String").create());
		options.addOption(OptionBuilder.withLongOpt("update-period")
				.withDescription("Schedule communication Peer-Peer and Tracker-Peer every update period")
				.hasArg().withArgName("Number").create());
		options.addOption(OptionBuilder.withLongOpt("pool-size")
				.withDescription("The pool size for Peer-Peer upload connection")
				.hasArg().withArgName("Number").create());
		options.addOption(OptionBuilder.withLongOpt("peer-connection-number")
				.withDescription("Limit Peer-Peer download conenction number")
				.hasArg().withArgName("Number").create());
		options.addOption(OptionBuilder.withLongOpt("ttl-search-file")
				.withDescription("The limit time for file searching through the network")
				.hasArg().withArgName("Numner").create());
		options.addOption(OptionBuilder.withLongOpt("log-verbosity")
				.withDescription("0 for low, 1 for medium and 2 for high")
				.hasArg().withArgName("Number").create());
		options.addOption(OptionBuilder.withLongOpt("upload-path")
				.withDescription("upload folder to share files")
				.hasArg().withArgName("String").create());
		options.addOption(OptionBuilder.withLongOpt("message-max-size")
				.withDescription("The maximum size of received message (Kilo Bytes)")
				.hasArg().withArgName("Number").create());
		
		return options;
	}

	private static void validateDirectory(String path) {
		File fl = new File(path);
		if (fl.exists()) {
			if (fl.isFile()) {
				generalLog.severe("File <" + path + "> already exists, try setting path in config file or renaming the file");
				System.exit(-1);
			}
		} else {
			fl.mkdir();
		}
	}

	private static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private static void setLog() throws SecurityException, IOException {
		downloadLog = Logger.getLogger(Constant.Log.DOWNLOAD_LOG);
		uploadLog = Logger.getLogger(Constant.Log.UPLOAD_LOG);
		generalLog = Logger.getLogger(Constant.Log.GENERAL_LOG);
		setHandler(downloadLog,"download.log");
		setHandler(uploadLog,"upload.log");
		setHandler(generalLog,"app.log");
	}
	
	private static void setHandler(Logger l, String logName) throws SecurityException, IOException {
		l.setUseParentHandlers(false);
		FileHandler fh = new FileHandler(logName);
		ConsoleHandler ch = new ConsoleHandler();
		Operation.setFomater(fh);
		Operation.setFomater(ch);
		ch.setLevel(Level.CONFIG);
		l.addHandler(fh);
		l.addHandler(ch);
		switch (Config.logVerbosity) {
		case 0: // low
			l.setLevel(Level.WARNING); 
			ch.setLevel(Level.WARNING);
			break;
		case 1: // medium
			l.setLevel(Level.CONFIG);
			ch.setLevel(Level.CONFIG);
			break;
		case 2: // high 
			l.setLevel(Level.FINE);
			ch.setLevel(Level.FINE);
			break;
		}
	}
}
