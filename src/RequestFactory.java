
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Classe RequestFactory
 * RequestFactory permet d'instancier une classe concrète de la classe abstraite Request
 * Le patron Factory est implementé
 */
public class RequestFactory {
	/**
	 * 
	 * @param className le nom de classe concrète
	 * @param in la chaine de caractère à passer au constructeur, et qui détermine la type de requete 
	 * @return une instance de className, le cast doit etre faire par l'appelant, null si une exception est levée
	 * @throws ProtocolException 
	 */
	
	@SuppressWarnings("unchecked")
	public static Request createRequest(String in) throws ProtocolException{
		char x = in.charAt(0);
		String className;
		
		switch(x) {
		case 'i' : className = InterestedRequestServer.class.getName();
		break;
		
		case 'g' : className = GetRequestServer.class.getName();
		break;
		
		case 'h' : className = HaveRequestServer.class.getName();
		break;
		
		default : throw new ProtocolException("Vérifier ton message");
		}
		
		@SuppressWarnings("rawtypes")
		Class c = null;
		try {
			c = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		@SuppressWarnings("rawtypes")
		Constructor ct;
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
