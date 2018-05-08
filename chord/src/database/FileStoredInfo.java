package database;

public class FileStoredInfo {

	private int fileId;
	private Boolean iAmResponsible;
	private Integer peerWhichRequested;
	
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

	public Integer getPeerWhichRequested() {
		return peerWhichRequested;
	}

	public void setPeerWhichRequested(Integer peerWhichRequested) {
		this.peerWhichRequested = peerWhichRequested;
	}


	
	
}
