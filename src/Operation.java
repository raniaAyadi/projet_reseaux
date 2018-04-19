import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.BitSet;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;

/**
 * Classe Operation
 * Operation impl�mente les focntions op�rationnelles indispensables comme des m�thodes static,
 * holds helper methods used by both server and client modules
 */

public class Operation {
    private static final String ABC="a9zert0yuiop81qsdf2ghjkl3mw45xc6vb7n";
    
    
    public static boolean testAddress(String ip,int port){
    	if(ip == null) return false;
    	try{
    		Socket s = new Socket(ip,port);
    		s.close();
    		return true;
    	}catch (Exception e){
    		return false;
    	}
    }
    
	public static String bitsetToString(BitSet b,int size){
		String ret = "";
		for(int i = 0 ; i < size;i ++){
			if(b.get(i)){
				ret+='1';
			}else{
				ret += '0';
			}
		}
		return ret;
	}
	
	public static BitSet stringToBitset(String s) {
		BitSet b = new BitSet(s.length());
		for(int i =0;i < s.length();i++	){
			if(s.charAt(i) == '1')
				b.set(i, true);
			else
				b.set(i,false);
		}
		return b;
	}
    
    
    
    /**
     * La fermeture du flux est g�r�e par l'appelant
     * @param in
     * @return
     * @throws IOException
     */
	@Deprecated
    public static String readInputStream(BufferedReader in) throws IOException {
    	int c;
    	String out = "";
    	
    	out = in.readLine();
    	return out;
    }
    
	@Deprecated
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
     * Lecture de contenu d'un fichier par caract�re
     * @param filePath
     * @return le contenu de fichier
     * @throws IOException si le fichier n'existe pas
     */
	@Deprecated
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
	 * @param s la chaine � crypter
	 * @param algo l'algorihtme de hacahge
	 * @return la cl�
	 */
	@Deprecated
    public static String getKey(String s, String algo){
        ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        passwordEncryptor.setAlgorithm(algo);
        passwordEncryptor.setPlainDigest( false );
        String motDePasseChiffre = passwordEncryptor.encryptPassword(s.toString() );
        return motDePasseChiffre;
    }

	public static boolean testListenPort(int port) {
		try{
			ServerSocket ss = new ServerSocket(port);
			ss.close();
			return true;
		}catch(IOException e){
			return false;
		}
	}

	public static int generateValidListenPort() {
		int index = 3000;
		while(true){
			try{
				ServerSocket ss = new ServerSocket(index);
				ss.close();
				break;
			}catch(IOException e ){
				index++;
			}
		}
		return index;
	}
    
}
