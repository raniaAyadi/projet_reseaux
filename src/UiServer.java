import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This class must expose all user actions over TCP to UI client
 * since UI is going to be built on browser, its better to have Json encoded response
 * No need for a multithreaded server since only one client is going to connect to it
 * @author Hmama Adem
 *
 */
public class UiServer implements Runnable {
	
	private void error(Socket s,PrintWriter writer, String error) {
		try {
			String res = "{\"error\":\""+ error +"\"}"; 
			writer.println(res);
			writer.flush();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// TODO: error handling + testing
	public void run() {
		ServerSocket s;
		try {
			
			
			s = new ServerSocket(8080);
			System.out.println("UI listening on port 8080");
			while (true) {
				Socket soc = s.accept();
				BufferedReader reader = new BufferedReader(new InputStreamReader(soc.getInputStream()));
				PrintWriter writer = new PrintWriter(soc.getOutputStream());
				String request = reader.readLine();
				System.out.println("requst : " + request);
				String[] dec = request.split(" ");
				Gson gson = new Gson();
				String res = "";
				if(dec[0].equals("startleech")){
					if(dec.length != 6){
						error(soc,writer,"Wrong number of arguments");
						continue;
					}	
					String filename = dec[1];
					long size = Long.parseLong(dec[2]);
					int pieceSize = Integer.parseInt(dec[3]);
					String key = dec[4];
					String path = dec[5];
					if(path.equals("null"))
						path = null;
					res = gson.toJson(UserAction.startLeech(filename, size, pieceSize, key, path));
					System.out.println("got request : " + filename + " " + size + " " + pieceSize + " " + key + " " + path);
					System.out.println("res: " + res);
				}else if(dec[0].equals("startseed")){
					String[] das = request.split("'");
					res = gson.toJson(UserAction.startSeed(das[1]));
				}else if(dec[0].equals("listall")){
					res = gson.toJson(UserAction.listAll());
				}else if(dec[0].equals("searchfiles")){
					String filename = dec[1];
					Integer minSize = Integer.parseInt(dec[2]);
					Integer maxSize = Integer.parseInt(dec[3]);
					if(minSize == -1)
						minSize = null;
					if(maxSize == -1)
						maxSize = null;
					res = gson.toJson(UserAction.searchFiles(filename, minSize, maxSize));
				}else if(dec[0].equals("getmanagedfiles")){
					gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
					// TODO : filetracker => @Expose
				}else if(dec[0].equals("getstats")){
					res = gson.toJson(UserAction.getStats());
				}else if(dec[0].equals("getbuffermap")){
					res = gson.toJson(UserAction.getBufferMap(dec[1]));
				}else if(dec[0].equals("removefile")){
					UserAction.removeFile(new Integer(dec[1]));
					res = gson.toJson("ok");
				}else if(dec[0].equals("pauseleech")){
					UserAction.pauseLeech(new Integer(dec[1]));
					res = gson.toJson("ok");
				}else if(dec[0].equals("resumeleech")){
					UserAction.resumeLeech(new Integer(dec[1]));
					res = gson.toJson("ok");
				}else{
					error(soc,writer,"Invalid request");
					continue;
				}
				
				writer.println(res);
				writer.flush();
				soc.close();
			}
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
