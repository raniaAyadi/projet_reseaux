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
	private int numberPieces;
	private BitSet bufferMap;
	
	/**
	 * Constructor to start seeding a new file, it hashes the name, sets the pieces, the bufferMap, size ...
	 * @param fileName just the name of the file to start seeding (not the path)  
	 * @throws NoSuchAlgorithmException 
	 */
	public FileTracker(String fileName) throws NoSuchAlgorithmException{

			// TODO: get download path from config
			String downloadPath = "C:\\Users\\msi\\Desktop\\app_downloads";
			String filePath = downloadPath + File.separator +fileName;
			this.fileName = fileName;
			size  =  (new File(filePath)).length();
			// TODO: if file size is too big, piece size should be also bigger that this value
			pieceSize = 4;
			// TODO: get hashing algorithm from config 
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			// TODO: utf_8 or utf_16 ??
			key = digest.digest(fileName.getBytes());
			numberPieces = (int) (size/pieceSize);
			if((size%pieceSize) !=0 )
				numberPieces++;
			System.out.println("nb pieces : " + numberPieces);
			bufferMap = new BitSet( numberPieces);
			bufferMap.set(0, numberPieces);
	}
	
	/**
	 * Constructor to start leeching a new file (generally gets called on user request for file download using a certain key)
	 * In this case the piece size, total size, nb of pieces ... has to be passed as argumet, program must get this data from the Tracker
	 * for each filetracker like this one, there should be a file download thread, which will constantly try to download the file content from other peers
	 * @param fileName
	 * 
	 */
	public FileTracker(String fileName,int size, int pieceSize,byte[] key){
		this.fileName = fileName;
		this.size = size;
		this.pieceSize = pieceSize;
		this.key = key;
		numberPieces = (int) (size/pieceSize);
		if((size%pieceSize) !=0 )
			numberPieces++;
		bufferMap = new BitSet(numberPieces);
		bufferMap.set(0,numberPieces,false);
	}
	
	
	public void writePiece(byte[]piece,int pieceIndex) throws Exception{
		Storage.writePiece(fileName, piece,(int)( pieceIndex * pieceSize) );
		bufferMap.set(pieceIndex);
	}
	
	
	// delete me 
	public void printBufferMap(){
		System.out.println("bufferMap:");
		for (int i = 0; i < numberPieces; i++) {
			if(bufferMap.get(i)){
				System.out.print("1");
			}else{
				System.out.print("0");
			}
		}
		System.out.println("");
	}
	
	public int getNumberPieces(){
		return numberPieces;
	}
	
	public byte[] getKey() {
		return key;
	}


	public void setKey(byte[] key) {
		this.key = key;
	}


	public long getSize() {
		return size;
	}


	public void setSize(long size) {
		this.size = size;
	}


	public long getPieceSize() {
		return pieceSize;
	}


	public void setPieceSize(long pieceSize) {
		this.pieceSize = pieceSize;
	}


	public BitSet getBufferMap() {
		return bufferMap;
	}


	public void setBufferMap(BitSet bufferMap) {
		this.bufferMap = bufferMap;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	
	
	public String getFileName(){
		return fileName;
	}
	

}
