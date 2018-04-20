import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

/**
 * Helper methods for trunk based file access, data is written/read in byte format 
 * @author Hmama Adem
 *
 */
public class Storage {

	/**
	 * If requested piece is the last piece in the file, chances are its size is less then pieceSize, in this case stuffing bits 
	 * are added
	 * @param filePath
	 * @param offset
	 * @param pieceSize
	 * @return 
	 * @throws Exception
	 */
	public static byte[] readPiece(String filePath,long offset,int pieceSize) throws Exception{
		byte[] ret = new byte[pieceSize];
		RandomAccessFile raf = new RandomAccessFile(filePath, "r");
		raf.seek(offset);
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
				System.out.println("File write failur, file is used by another process");
			}
		}
	
	}
}
