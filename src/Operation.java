import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;

/**
 * Classe Operation
 * Operation implémente les focntions opérationnelles indispensables comme des méthodes static
 */

public class Operation {
    private static final String ABC="a9zert0yuiop81qsdf2ghjkl3mw45xc6vb7n";
    
    /**
     * La fermeture du flux est gérée par l'appelant
     * @param in
     * @return
     * @throws IOException
     */
    public static String readInputStream(BufferedReader in) throws IOException {
    	int c;
    	String out = "";
    	
    	out = in.readLine();
    	return out;
    }
    
    public static String readPart(String filePath,int cutSize, int numPart) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(filePath)));
		byte[] tab;
		int bytes;
		int c;
		int size;
		String out;
		
		bytes = cutSize * (numPart - 1);
		tab = new byte[bytes];
		
		in.read(tab, 0, bytes);
		out = "";
		while((c = in.read()) != -1 && cutSize > 0){
			out += (char) c;
			cutSize--;
		}
		
		if(in != null) in.close();
		return out;
	}
    
    /**
     * Lecture de contenu d'un fichier par caractère
     * @param filePath
     * @return le contenu de fichier
     * @throws IOException si le fichier n'existe pas
     */
	public static String readFile(String filePath) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(filePath)));
		String out = "";
		int c;
		
		while((c = in.read()) != -1) 
			out += (char)c;

		if(in != null) in.close();
		
		return out;
	}
     
	/**
	 * 
	 * @param s la chaine à crypter
	 * @param algo l'algorihtme de hacahge
	 * @return la clé
	 */
    public static String getKey(String s, String algo){
        ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        passwordEncryptor.setAlgorithm(algo);
        passwordEncryptor.setPlainDigest( false );
        String motDePasseChiffre = passwordEncryptor.encryptPassword(s.toString() );
        return motDePasseChiffre;
    }
    
}
