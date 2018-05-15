package peer.storage;

import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.security.NoSuchAlgorithmException;
import com.google.gson.annotations.Expose;
import peer.Config;
import peer.Operation;
import peer.server.PieceNotAvailableException;

/**
 * This class is responsible for keeping track of the file content + update on
 * demand its content on disk Each file should have its own fileTracker, whether
 * it has finished downloading(seeding), or have just started This object state
 * is restored after application shutdown based on the serialization file of
 * this fileTracker placed in the .meta folder
 *
 * @author Adem Hmama
 * @version 1.0
 *
 */
@SuppressWarnings("serial")
public class FileTracker implements java.io.Serializable {

	@Expose
	public int id;

	@Expose
	private String key;
	@Expose
	private String fileName;
	@Expose
	private String filePath;             // full path (with the name in it)
	@Expose
	private long size;                   // in bytes
	@Expose
	private int pieceSize;               // in bytes
	@Expose
	private int numberPieces;
	private Integer totalReached; 		 // speed up isSeeding method
	private String bufferMap;
	private boolean suspended;
	public transient Object suspendLock; // need separate lock for pause/resume
	@Expose
	private int maxBytes; 				 // max allowed in one cycle
	private int currBytes;    	         // bytes per cycle
	public transient Object statLock;
	private boolean stop;           	 // used to terminate fileDownloader thread (without
							        	 // using the deprecated stop function)

	/**
	 * Constructor to start seeding a new file, it hashes the name, sets the
	 * pieces, the bufferMap, size ...
	 * 
	 * @param fileName just the name of the file to start seeding (not the path)
	 * @throws NoSuchAlgorithmException
	 */
	public FileTracker(File fl) throws NoSuchAlgorithmException {
		this.filePath = fl.getAbsolutePath();
		this.fileName = fl.getName();
		size = fl.length();
		pieceSize = generatePieceSize(size);
		key = Operation.encryptFileName(fileName);
		numberPieces = (int) (size / pieceSize);
		if ((size % pieceSize) != 0)
			numberPieces++;
		totalReached = numberPieces;

		CharBuffer aux = CharBuffer.allocate(numberPieces);
		for (int i = 0; i < numberPieces; i++)
			aux.put(i, '1');
		this.bufferMap = aux.toString();
		System.out.println("FileTracker bufferMap "+bufferMap);
		
		suspendLock = new Object();
		statLock = new Object();
		stop = false;
		Config.generalLog.info("start seeding file: <" +fileName+":"+key+">"  );
	}

	/**
	 * Constructor to start Leeching a new file (generally gets called on user
	 * request for file download using a certain key) In this case the piece
	 * size, total size, number of pieces ... has to be passed as argument,
	 * program must get this data from the Tracker for each FileTracker like
	 * this one, there should be a FileDownloader thread, which will constantly
	 * try to download the file content from other peers
	 * 
	 * @param fileName
	 * @param size
	 * @param pieceSize
	 * @param key
	 * @param path
	 *            where to store the file (without filename (not the fullPath))
	 * @throws Exception
	 */
	public FileTracker(String fileName, long size, int pieceSize, String key, String path) throws Exception {
		this.filePath = path + File.separator + fileName;
		File fl = new File(filePath);
		if (fl.exists())
			throw new Exception("file " + filePath + "already exists");
		else
			fl.createNewFile();
		suspended = false;
		this.fileName = fileName;
		this.size = size;
		this.pieceSize = pieceSize;
		this.key = key;
		numberPieces = (int) (size / pieceSize);
		totalReached = 0;
		if ((size % pieceSize) != 0)
			numberPieces++;

		CharBuffer aux = CharBuffer.allocate(numberPieces);
		for (int i = 0; i < numberPieces; i++)
			aux.put(i, '0');
		this.bufferMap = aux.toString();

		suspendLock = new Object();
		maxBytes = -1; // unlimited
		currBytes = 0;
		statLock = new Object();
		stop = false;
		Config.generalLog.info("start leeching file: <" +fileName+":"+key+">" );
	}

	/**
	 * Thread safe
	 * 
	 * @param maxBytes maximum number of bytes per second
	 */
	public void setDownSpeed(int maxBytes) {
		synchronized (statLock) {
			this.maxBytes = maxBytes;
			currBytes = 0;
		}
	}

	/**
	 * Thread safe Mark FileTracker as non managed (simulate signal to end  FileDownloader)
	 * 
	 * @return
	 */
	public void terminate() {
		synchronized (this) {
			stop = true;
		}
	}

	/**
	 * Thread safe Indicates to the FileDownloader that it must stop downloading
	 * 
	 * @return
	 */
	public boolean isTerminated() {
		synchronized (this) {
			return stop;
		}
	}

	/**
	 * Thread safe Unset download speed limit
	 */
	public void unsetDownSpeed() {
		synchronized (statLock) {
			maxBytes = -1;
		}
	}

	/**
	 * Thread safe check if fileTracker supports more pieces in the current cycle
	 * 
	 * @return
	 */
	public boolean downloadAllowed() {
		synchronized (statLock) {
			if (maxBytes == -1)
				return true;
			return currBytes <= maxBytes;
		}
	}

	/**
	 * Thread safe Resets the currBytes to 0 (initiate stat of the next cycle)
	 * 
	 * @return the currBytes before reset
	 */
	public int resetAndGet() {
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
	 * 
	 * @param piece
	 * @param pieceIndex
	 */
	public void addPiece(byte[] piece, int pieceIndex) {
		if (pieceIndex < 0 || pieceIndex >= numberPieces)
			throw new IndexOutOfBoundsException();

		// trim the fat in case of last piece
		if (pieceIndex == (numberPieces - 1)) {
			int chunksize = (int) (pieceSize - (numberPieces * pieceSize - size));
			byte[] aux = new byte[chunksize];
			System.arraycopy(piece, 0, aux, 0, chunksize);
			piece = aux;
		}
		try {
			Storage.writePiece(fileName, piece, (int) (pieceIndex * pieceSize));
			if (this.has(pieceIndex)) {
				Config.downloadLog.warning("re-writing the same piece ??");
				Config.downloadLog.warning("this will affect the total reached, (non valid res)");
			}
			synchronized (bufferMap) {
				CharBuffer aux = CharBuffer.allocate(numberPieces);
				int size = bufferMap.length();
				for (int i = 0; i < size; i++)
					aux.put(i, bufferMap.charAt(i));
				aux.put(pieceIndex, '1');
				bufferMap = aux.toString();
			}
			synchronized (totalReached) {
				totalReached++;
			}
			synchronized (statLock) {
				currBytes += pieceSize;
			}

		} catch (IOException e) {
			Config.downloadLog.fine("error writing piece for file <"+fileName+"> with index: " + pieceIndex);
		}
	}

	/**
	 * Thread safe
	 * 
	 * @param pieceIndex
	 * @return
	 * @throws PieceNotAvailableException
	 * @throws IOException
	 */
	public byte[] getPiece(int pieceIndex) throws PieceNotAvailableException, IOException {
		if (pieceIndex >= numberPieces || pieceIndex < 0)
			throw new IndexOutOfBoundsException();
		synchronized (bufferMap) {
			if (bufferMap.charAt(pieceIndex) == '0') {
				throw new PieceNotAvailableException();
			}
		}
		try {
			return Storage.readPiece(filePath, pieceIndex * pieceSize, pieceSize);
		} catch (IOException e) {
			Config.uploadLog.warning("Error when reading piece from <" + fileName + ">");
			throw e;
		}
	}

	/**
	 * Thread safe indicates if piece with index 'index' is available
	 * 
	 * @param index
	 * @return
	 */
	public boolean has(int index) {
		if (index < 0 || index >= numberPieces) {
			throw new IndexOutOfBoundsException();
		}
		synchronized (bufferMap) {
			return bufferMap.charAt(index) == '1';
		}
	}

	/**
	 * Thread safe
	 */
	public void pause() {
		synchronized (suspendLock) {
			suspended = true;
		}
	}

	/**
	 * Thread safe
	 */
	public void resume() {
		synchronized (suspendLock) {
			if (suspended)
				suspendLock.notify(); // FileDownloader waiting on it ..
			suspended = false;
		}
	}

	/**
	 * Thread safe
	 * 
	 * @return
	 */
	public boolean isSuspended() {
		synchronized (suspendLock) {
			return suspended;
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public boolean isSeeding() {
		return totalReached == numberPieces;
	}

	/**
	 * Piece size must be proportional to total size, going from 16kb min to
	 * 16384kb (copycat utorrent implementation)
	 * 
	 * @param size
	 * @return
	 */
	private int generatePieceSize(long size) {
		int KB = 1024;
		int MB = 1024 * KB;
		String confVal = Config.propreties.getProperty("piece-size");
		if (confVal != null) {
			try {
				int ret = Integer.parseInt(confVal);
				if (!(ret >= 64 && ret <= MB))
					throw new Exception();
				return ret;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (size < 10 * MB)   return 16 * KB;
		if (size < 100 * MB)  return 64 * KB;
		if (size < 500 * MB)  return 128 * KB;
		if (size < 800 * MB)  return 256 * KB;
		if (size < 1024 * MB) return 512 * KB;
		return MB;
	}

	public double getPercentage() {
		double tot = (double) totalReached;
		double nb = (double) numberPieces;
		return (tot / nb) * 100;
	}

	public int getNumberPieces() {
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
	 * 
	 * @return
	 */
	public String getBuffermap() {
		return bufferMap;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean hasPart() {
		int size = bufferMap.length();
		for (int i = 0; i < size; i++) {
			if (bufferMap.charAt(i) == '1') {
				return true;
			}
		}
		return false;
	}

}
