package database;

public class BackupRequest {

	private String fileId;
	private String filename;
	private String encryptKey;
	private Integer numberOfChunks; 

	public BackupRequest(String fileId, String filename) {
		this.fileId = fileId;
		this.filename = filename;
		this.encryptKey = null;
		this.setNumberOfChunks(null);
	}
	
	public BackupRequest(String fileId, String filename, String encryptKey, Integer numberOfChunks) {
		this.fileId = fileId;
		this.filename = filename;
		this.encryptKey = encryptKey;
		this.setNumberOfChunks(numberOfChunks);
	}

	public String getFileId() {
		return fileId;
	}

	public String getFilename() {
		return filename;
	}

	public String getEncryptKey() {
		return encryptKey;
	}

	/**
	 * @return the numberOfChunks
	 */
	public Integer getNumberOfChunks() {
		return numberOfChunks;
	}

	/**
	 * @param numberOfChunks the numberOfChunks to set
	 */
	public void setNumberOfChunks(Integer numberOfChunks) {
		this.numberOfChunks = numberOfChunks;
	}

}
