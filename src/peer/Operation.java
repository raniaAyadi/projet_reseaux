package peer;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.BitSet;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;


/**
 * Classe Operation
 * Operation impl�mente les focntions op�rationnelles indispensables comme des m�thodes static,
 * holds helper methods used by both server and client modules
 */

public class Operation {
    
	public static String bitsetToString(BitSet b){
		String ret = "";
		int size = b.length();
		
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
    
	public static void setFomater(FileHandler f) {
		f.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
     });
	}
	
	public static String encryptFileName(String name) {
		ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
		passwordEncryptor.setAlgorithm("SHA-1");
		passwordEncryptor.setPlainDigest(true);
		String key = passwordEncryptor.encryptPassword(name);
		
		return key;
	}
}
