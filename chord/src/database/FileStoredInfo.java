package database;

public class FileStoredInfo {

	private int fileId;
	private Boolean iAmResponsible;
	private Short peerWhichRequested;
	
	public FileStoredInfo(int fileId, Boolean iAmResponsible){
		this.fileId = fileId;
		this.iAmResponsible = iAmResponsible;
	}

	public int getFileId() {
		return fileId;
	}

	public Boolean getiAmResponsible() {
		return iAmResponsible;
	}

	public Short getPeerWhichRequested() {
		return peerWhichRequested;
	}

	public void setPeerWhichRequested(short s) {
		this.peerWhichRequested = s;
	}


	
	
}
