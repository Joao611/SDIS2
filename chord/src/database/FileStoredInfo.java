package database;

import java.math.BigInteger;

public class FileStoredInfo {

	private String fileId;
	private Boolean iAmResponsible;
	private Short peerWhichRequested;
	
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

	public Short getPeerWhichRequested() {
		return peerWhichRequested;
	}

	public void setPeerWhichRequested(short s) {
		this.peerWhichRequested = s;
	}


	
	
}
