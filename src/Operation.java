import java.io.IOException;
import java.net.ServerSocket;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    
}
