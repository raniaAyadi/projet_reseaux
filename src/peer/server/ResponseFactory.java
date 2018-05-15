package peer.server;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ResponseFactory {
	
	@SuppressWarnings("unchecked")
	public static Response createResponse(Request req, OutputStream out) throws ProtocolException {
		String className = null;
		if(req instanceof InterestedRequestServer) {
			className = InterestedResponseServer.class.getName();
		}
		else if(req instanceof GetRequestServer) {
			className = GetResponseServer.class.getName();
		}
		else if(req instanceof HaveRequestServer) {
			className = HaveResponseServer.class.getName();
		}
		
		@SuppressWarnings("rawtypes")
		Class c = null;
		Map<String, Object>fields = req.getFields();
		
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		@SuppressWarnings("rawtypes")
		Constructor ct;
		try {
			ct = c.getConstructor(OutputStream.class, Map.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		try {
			return (Response)ct.newInstance(out, fields);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
		catch(InvocationTargetException e) {
			throw new ProtocolException(e.getCause().getMessage());
		}
		
	}
}
