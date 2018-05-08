package database;

public class FileStoredInfo {

	private String fileId;
	private Boolean iAmResponsible;
	private String peerRequesting;
	
	public FileStoredInfo(String fileId, Boolean iAmResponsible){
		this.fileId = fileId;
		this.iAmResponsible = iAmResponsible;
	}

	public String getFileId() {
		return fileId;
	}

	public Boolean getiAmResponsible() {
		return iAmResponsible;
	}

	public String getPeerRequesting() {
		return peerRequesting;
	}

	public void setPeerRequesting(String peerId) {
		this.peerRequesting = peerId;
	}


	
	
}
