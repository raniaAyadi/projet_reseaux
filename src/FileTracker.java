import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

import org.jasypt.util.password.BasicPasswordEncryptor;



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

	public int id; // identification can be done only using keys, this will act as a local (to the peer itself) way of identification, making it eazier and more efficient 
	// especially when communicating with UI which includes many status update requests, sending the hole key each time consumes bandwidth and reduces refresh speed, ids are not
	// generated by the constructor itself, but it is set by the application context
	private String key;
	private String fileName;
	private String filePath;
	private long size; // in bytes
	private int  pieceSize; // in bytes
	private int numberPieces;
	private int totalReached; // speed up isSeeding method
	private BitSet bufferMap;
	private boolean suspended;
	public transient Object suspendLock; // needed seprate lock for pause/resume
	
	private int maxBytes; // max allowed in one cyle
	private int currBytes; // bytes per cycle
	public transient Object statLock;
	

	/**
	 * Constructor to start seeding a new file, it hashes the name, sets the pieces, the bufferMap, size ...
	 * @param fileName just the name of the file to start seeding (not the path)
	 * @throws NoSuchAlgorithmException
	 */
	public FileTracker(String fileName,String filePath) throws NoSuchAlgorithmException{
			this.filePath = filePath;
			this.fileName = fileName;
			size  =  (new File(filePath)).length();
			// TODO: if file size is too big, piece size should be also bigger that this value
			pieceSize = generatePieceSize(size);
			key = (new BasicPasswordEncryptor()).encryptPassword(fileName);	
			numberPieces = (int) (size/pieceSize);
			if((size%pieceSize) !=0 )
				numberPieces++;
			totalReached = numberPieces; 
			System.out.println("nb pieces : " + numberPieces);
			bufferMap = new BitSet( numberPieces);
			bufferMap.set(0, numberPieces);
			suspendLock = new Object(); // not necessary, no file downloader will be instaciated ! 
			statLock = new Object();
	}

	/**
	 * Constructor to start leeching a new file (generally gets called on user request for file download using a certain key)
	 * In this case the piece size, total size, number of pieces ... has to be passed as argument, program must get this data from the Tracker
	 * for each filetracker like this one, there should be a file downloader thread, which will constantly try to download the file content from other peers
	 * 
	 * @throws Exception 
	 *
	 */
	public FileTracker(String fileName,long size, int pieceSize,String key,String path) throws Exception{
		this.filePath = path + File.separator + fileName;
		File fl = new File(filePath);
		if(fl.exists())
			throw new Exception("file " + filePath + "already exists");
		else
			fl.createNewFile();
		suspended = false;
		this.fileName = fileName;
		this.size = size;
		this.pieceSize = pieceSize;
		this.key = key;
		numberPieces = (int) (size/pieceSize);
		totalReached = 0;
		if((size%pieceSize) !=0 )
			numberPieces++;
		bufferMap = new BitSet(numberPieces);
		bufferMap.set(0,numberPieces,false);
		suspendLock = new Object();
		maxBytes = -1; // unlimeted
		currBytes = 0;
		statLock = new Object();
	}
	
	/**
	 * Thread safe
	 * @param maxBytes maximum number of bytes per second
	 */
	public void setDownSpeed(int maxBytes){
		// TODO : error checking
		synchronized (statLock) {
			this.maxBytes = maxBytes;
			currBytes = 0;
		}
	}
	
	/**
	 * Thread safe
	 * Unset download speed limit 
	 */
	public void unsetDownSpeed(){
		synchronized (statLock) {
			maxBytes = -1;
		}
	}
	
	/**
	 * Thread safe
	 * check if fileTracker supports more pieces in the current cyle
	 * @return
	 */
	public boolean downloadAllowed(){
		synchronized (statLock) {
			if(maxBytes == -1)
				return true;
			return currBytes <= maxBytes;
		}
	}
	
	/**
	 * thread safe
	 * Resets the currBytes to 0 (initiate stat of the next cycle) 
	 * @return the currBytes before reset
	 */
	public int resetAndGet(){
		int ret;
		synchronized (statLock) {
			ret = currBytes;
			currBytes = 0;
			statLock.notify(); // fileDownloader waiting
		}
		return ret;
	}
	

	/**
	 * Thread safe
	 * @param piece
	 * @param pieceIndex
	 */
	public void addPiece(byte[]piece,int pieceIndex) {
		if(pieceIndex<0 || pieceIndex >=numberPieces)
			throw new IndexOutOfBoundsException();
		// TODO : exception if non valid piece size
		// trim the fat in case of last piece
		if(pieceIndex == (numberPieces -1)){
			int chunksize = (int) (pieceSize - (numberPieces*pieceSize - size)) ;
			byte[] aux = new byte[chunksize];
			System.arraycopy(piece, 0, aux, 0, chunksize);
			piece = aux;
		}
		try{
			Storage.writePiece(fileName, piece,(int)( pieceIndex * pieceSize) );	
			totalReached++;
			if(this.has(pieceIndex)){
				// TODO: remove this after test
				System.err.println("filetracker : re-writing the same piece ??");
				System.err.println("this will affect the total reached, (non valid res)"); 
			}
			synchronized (bufferMap) {
				bufferMap.set(pieceIndex);
			}
			synchronized (statLock) {
				currBytes+= pieceSize;
			}
			
		}catch (IOException e){
			//System.err.println("error writing piece with index: " + pieceIndex);
		}	
	}

	/**
	 * Thread safe
	 * @param pieceIndex
	 * @return
	 * @throws PieceNotAvailableException
	 * @throws IOException
	 */
	public byte[] getPiece(int pieceIndex) throws PieceNotAvailableException, IOException  {
		if(pieceIndex >= numberPieces || pieceIndex <0 )
			throw new IndexOutOfBoundsException();
		synchronized (bufferMap) {
			if(!bufferMap.get(pieceIndex)){
				throw new PieceNotAvailableException();
			}			
		}
		try{
			return Storage.readPiece(filePath, pieceIndex*pieceSize, pieceSize);
		}catch(IOException e){
			System.err.println("error when reading piece");
			throw e;
		}
	}
	
	/**
	 * Thread safe
	 * indicates if piece with index 'index' is available
	 * 
	 * @param index
	 * @return
	 */
	public boolean has(int index){
		if(index <0 || index >= numberPieces){
			throw new IndexOutOfBoundsException();
		}
		synchronized (bufferMap) {
			return bufferMap.get(index);
		}
	}
	
	/**
	 * Thread safe
	 */
	public void pause(){
		synchronized (suspendLock) {
			suspended = true;
		}
	}
	
	/**
	 * Thread safe
	 */
	public void resume(){
		synchronized (suspendLock) {
			if(suspended)
				suspendLock.notify(); // filedownloader waiting on it ..	
			suspended = false;
		}
	}
	
	/**
	 * Thread safe
	 * @return
	 */
	public boolean isSuspended(){
		synchronized (suspendLock) {
			return suspended;
		}
	}


	public boolean isSeeding(){
		// no need to lock, only filedownloader updates and checks for its value
		return totalReached == numberPieces;
	}

	/**
	 * Piece size must be proportinal to total size, going from 16kb min to 16384kb (copycat utorrent implementation)
	 * @param size
	 * @return
	 */
	private int generatePieceSize(long size){
		int KB = 1024;
		int MB = 1024*KB;
		String confVal = Config.propreties.getProperty("piece-size");
		if(confVal != null){
			try{
				int ret =  Integer.parseInt(confVal);
				if(!(ret >= 64 && ret <= MB))
					throw new Exception();
				return ret;
			}catch(Exception e){
				System.out.println("warning, invalid piece size specified in config file");
			}
		}
	
		if(size < 2*MB)    return 64; // TODO: remove this, i've added it just for testing
		if(size < 10*MB)   return 16*KB;
		if(size < 100*MB)  return 64*KB;
		if(size < 500*MB)  return  128*KB;
		if(size < 800*MB)  return 256*KB;
		if(size < 1024*MB) return 512*KB;
		return MB;
	}
	
	public double getPercentage(){
		// TODO : add synchronisation when accessing totalReached
		double tot = (double) totalReached;
		double nb = (double ) numberPieces;
		return (tot/nb)*100;
	}
	

	// DEBUG
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

	public String getKey() {
		return key;
	}

	public long getSize() {
		return size;
	}

	public int getPieceSize() {
		return pieceSize;
	}

	/**
	 * Thread safe
	 * @return
	 */
	public String getBuffermap(){
		synchronized (bufferMap) {
			String ret = "";
			int size = bufferMap.length();
			
			for(int i = 0 ; i < size;i ++){
				if(bufferMap.get(i)){
					ret+='1';
				}else{
					ret += '0';
				}
			}
			return ret;
		}
	}


	public String getFileName(){
		return fileName;
	}

}
