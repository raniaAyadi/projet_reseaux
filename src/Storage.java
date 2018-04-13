import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class Storage {

	
	public static byte[] readPiece(String filePath,int offset,int pieceSize) throws Exception{
		byte[] ret = new byte[pieceSize];
		RandomAccessFile raf = new RandomAccessFile(filePath, "r");
		raf.seek(offset);
		raf.read(ret);
		raf.close();
		return ret;
	}
	
	public static void writePiece(String filePath,byte[] piece,int offset)throws Exception{	
		// TODO : update to support files containing holes 
		RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
		raf.seek(offset);
		raf.write(piece);
		raf.close();
	}
}
