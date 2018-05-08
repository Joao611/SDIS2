package database;

public class ChunkInfo {

	private Integer chunkId;
	private String fileId;
	
	public ChunkInfo(Integer chunkId,String fileId) {
		this.chunkId = chunkId;
		this.fileId = fileId;
	}
	
	public Integer getChunkId() {
		return chunkId;
	}
	
	public String getFileId() {
		return fileId;
	}
	
}
