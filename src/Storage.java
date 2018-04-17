import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class Storage {

	
	public static byte[] readPiece(String filePath,long l,int pieceSize) throws Exception{
		byte[] ret = new byte[pieceSize];
		RandomAccessFile raf = new RandomAccessFile(filePath, "r");
		raf.seek(l);
		raf.read(ret);
		raf.close();
		return ret;
	}
	
	public static void writePiece(String filePath,byte[] piece,int offset) {	
		// TODO : update to support files containing holes 
		while(true){
			try{
				RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
				raf.seek(offset);
				raf.write(piece);
				raf.close();
				break;
			}catch (Exception e){
				System.out.println("File write failer, file is used by another process");
			}
		}
	
	}
}
