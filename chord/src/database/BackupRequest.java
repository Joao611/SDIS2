package database;

public class BackupRequest {

	private String fileId;
	private String filename;
	private String encryptKey;

	public BackupRequest(String fileId, String filename) {
		this.fileId = fileId;
		this.filename = filename;
		this.encryptKey = null;
	}
	
	public BackupRequest(String fileId, String filename, String encryptKey) {
		this.fileId = fileId;
		this.filename = filename;
		this.encryptKey = encryptKey;
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

}
