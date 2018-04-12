import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;



/**
 * This class is responsible for keeping track of the file content +  update on demand its content on disk 
 * Each file should have its own fileTracker, whether it has finished downloading(seeding), or have just started 
 * This object state is restored after application shutdown based on the serialisation file of this fileTracker palced in the .meta folder
 * 
 * @author Adem Hmama
 * @version 1.0
 * 
 * */
public class FileTracker implements java.io.Serializable   {

	
	private String fileName;
	private byte[]  key;
	private long size; // in bytes
	private long pieceSize; // in bytes
	private BitSet bufferMap;
	
	/**
	 * Its function depends on the metaData value
	 * @param fileName just the name of the file (not the path)  
	 * @param metaData if it is true, that means that the file has its own metadata file and it needs to parse that file
	 * to get all file parameters, if metaData is false, this means that only the file on the download path
	 * with no tracking metadata file, this means that the user wants to share a new file on the network, a new metadata file is created 
	 * in this case
	 * @throws NoSuchAlgorithmException 
	 */
	public FileTracker(String fileName,boolean metaData) throws NoSuchAlgorithmException{
		if(metaData){
			// parse
			// set attributes
		}else{ // new file to be shared
			// TODO: get download path from config
			String downloadPath = "C:\\Users\\msi\\Desktop\\app_downloads";
			String filePath = downloadPath + File.separator +fileName;
			this.fileName = fileName;
			size  =  (new File(filePath)).length();
			// TODO: if file size is too big, piece size should be also bigger that this value
			pieceSize = 1024;
			// TODO: get hashing algorithm from config 
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			// TODO: utf_8 or utf_16 ??
			key = digest.digest(fileName.getBytes(StandardCharsets.UTF_8));
		}
	}
	
	
	/**
	 * Instanciate a fileTracker for a new file to be downloaded from the internet
	 * In this case the piece size, total size, nb of pieces ... has to be passed as argumet, program must get this data from the Tracker
	 * @param fileName
	 */
	public FileTracker(String fileName,int pieceSize,int nbPieces){
		// code
	}
	
	//TODO: getters and setters
	
	public void writePiece(byte[]piece,int pieceNumber) throws Exception{
		if(pieceNumber == (size/pieceSize)){
			// last piece !
			return ;
		}
		
		//Storage.writePiece(fileName, piece, (pieceNumber-1) * pieceSize );
	}
	
	public String getFileName(){
		return fileName;
	}
	

}
