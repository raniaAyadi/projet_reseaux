import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ResponseFactory {
	
	public static Response createResponse(String className, Map<String, Object> fields) throws ProtocolException {
		Class<?> c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		Constructor<?> ct;
		try {
			ct = c.getConstructor(Map.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		try {
			return (Response)ct.newInstance(fields);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
		catch(InvocationTargetException e) {
			throw new ProtocolException(e.getCause().getMessage());
		}
		
	}
}
