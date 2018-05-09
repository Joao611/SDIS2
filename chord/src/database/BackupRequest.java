package database;

public class BackupRequest {

	private String fileId;
	private String filename;
	private String encryptKey;
	private Integer desiredRepDegree;

	public BackupRequest(String fileId, String filename, Integer desiredRepDegree) {
		this.fileId = fileId;
		this.filename = filename;
		this.encryptKey = null;
		this.desiredRepDegree = desiredRepDegree;
	}
	
	public BackupRequest(String fileId, String filename, String encryptKey, Integer desiredRepDegree) {
		this.fileId = fileId;
		this.filename = filename;
		this.encryptKey = encryptKey;
		this.desiredRepDegree = desiredRepDegree;
	}
	
	public Integer getDesiredRepDegree() {
		return desiredRepDegree;
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
