import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Classe RequestFactory
 * RequestFactory permet d'instancier une classe concr�te de la classe abstraite Request
 * Le patron Factory est implement�
 */
public class RequestFactory {
	/**
	 * 
	 * @param className le nom de classe concr�te
	 * @param in la chaine de caract�re � apsser au constructeur 
	 * @return une instance de className, le cast doit etre faire par l'appelant, null si une exception est lev�e
	 * @throws ProtocolException 
	 */
	public static Request createRequest(String className, String in) throws ProtocolException{
		Class<?> c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		Constructor<?> ct;
		try {
			ct = c.getConstructor(String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			return (Request)ct.newInstance(in);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
		catch(InvocationTargetException e) {
			throw new ProtocolException(e.getCause().getMessage());
		}
		
	}
}
