
public class FileInfo {

	public String fileName;
	public long fileSize;
	public int pieceSize;
	public String key;
	
	public FileInfo(String filename, String size,String pieceSize, String key) throws Exception{
		// TODO: data validation, error handling
		fileName = filename;
		fileSize = Integer.parseInt(size);
		this.pieceSize = Integer.parseInt(pieceSize);
		this.key = key;
	}
}
