package database;

public class ChunkInfo {

	private Integer chunkId;
	private Integer fileId;
	
	public ChunkInfo(Integer chunkId, Integer fileId) {
		this.chunkId = chunkId;
		this.fileId = fileId;
	}
	
	public Integer getChunkId() {
		return chunkId;
	}
	
	public Integer getFileId() {
		return fileId;
	}
	
}
