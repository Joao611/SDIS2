package state_info;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import state_info.Chunk.State;


public class BackupFile {

	private String pathName = null;
	private int serviceID = 0;
	private int replicationDeg = 0;
	private Map<Integer,Chunk> chunks = new ConcurrentHashMap<Integer, Chunk>();
	private boolean wasDeleted = false;
	
	/**
	 * @return the wasDeleted
	 */
	public boolean wasDeleted() {
		return wasDeleted;
	}

	public BackupFile(String pathName, int serviceID, int replicationDeg) {
		this.pathName = pathName;
		this.serviceID = serviceID;
		this.replicationDeg = replicationDeg;
	}
	
	/**
	 * 
	 * @return the file pathname
	 */
	public String getPathName() {
		return this.pathName;
	}
	
	/**
	 * 
	 * @return the backup service id of the file
	 */
	public int getServiceID() {
		return this.serviceID;
	}
	
	/**
	 * 
	 * @return the desired replication degree
	 */
	public int getReplicationDegree() {
		return this.replicationDeg;
	}
	
	/**
	 * stores the info of one chunk of this file
	 * @param chunk
	 */
//	public BackupFile addChunk(Chunk chunk) {
//		if(chunks.get(chunk.getID()) ==  null){
//			chunks.put(chunk.getID(), chunk);
//			LocalState.getInstance().setUsedStorage(chunk.getSize());
//			return this;
//		} else {
//			if(chunks.get(chunk.getID()).isNewPeerStoring(Peer.id)) {
//				LocalState.getInstance().setUsedStorage(chunk.getSize());
//			}
//			return null;
//		}
//
//	}

	/**
	 * verifies if  the actual replication degree of a chunk is lower than the one that is desired
	 * @return
	 */
	public boolean desireReplicationDeg(int chunkID) {
		return chunks.get(chunkID).desireReplicationDeg();
	}
	
	/**
	 * increases the replication degree if there is a new peer storing the chunk
	 * @param chunkID
	 * @param senderID
	 * @return
	 */
	public boolean updateReplicationInfo(int chunkID, int senderID) {
		Chunk chunk = getChunks().get(chunkID);
		if(chunk == null) {
			return false;
		} 
		return chunk.isNewPeerStoring(senderID);
	}

	public boolean seeIfAlreadySent(int chunkID) {
		return getChunks().get(chunkID).seeIfAlreadySent();
	}

	public void notifyThatItWasSent(int chunkNo) {
		getChunks().get(chunkNo).notifyThatItWasSent();		
	}

	public void returnToFalse(int chunkNo) {
		getChunks().get(chunkNo).returnToFalse();		
		
	}

	/**
	 * @return the chunks
	 */
	public Map<Integer,Chunk> getChunks() {
		return chunks;
	}

	/**
	 * @param chunks the chunks to set
	 */
	public void setChunks(Map<Integer,Chunk> chunks) {
		this.chunks = chunks;
	}
	/**
	 * increases by one the current replication degree
	 * @param chunkID
	 */
	public void increaseReplicationDegree(int chunkID) {
		chunks.get(chunkID).increaseReplicationDeg();
	}
	
	/**
	 * decreases by one the current replication degree
	 * @param chunkID
	 * @param peerID who is going to stop storing the chunk
	 * @return the size of the chunk (freed space)
	 */
//	public int decreaseReplicationDegree(int chunkID, int peerID) {
//		int freedStorage = 0;
//		if(isStoringChunk(chunkID)) {
//			freedStorage = (int) getChunks().get(chunkID).getSize();
//		}
//		getChunks().get(chunkID).decreaseReplicationDeg(peerID);
//		
//		return freedStorage;
//	}
	
	public void decreaseReplicationDegree(int peerID) {
		for (Entry<Integer, Chunk> entry : chunks.entrySet()) {
			entry.getValue().decreaseReplicationDeg(peerID);
		}
	}
	
	/**
	 * Free storage
	 * @return the total space used saving this chunks
	 */
//	public int deleteChunks() {
//		int totalSpace = 0;
//
//		for (ConcurrentHashMap.Entry<Integer, Chunk> entry : chunks.entrySet()) {
//			if(isStoringChunk(entry.getKey())) {
//				Chunk value = entry.getValue();
//				totalSpace += value.getSize();
//			}
//		}
//		
//		return totalSpace;
//	}
	
	/**
	 * deletes one chunk
	 * @param chunkID
	 * @return the size of the chunk
	 */
//	public long deleteChunk(int chunkID) {
//		if(isStoringChunk(chunkID)) {
//			long freedSpace = chunks.get((Integer)chunkID).getSize();
//			chunks.remove((Integer)chunkID);
//
//			return freedSpace;
//		}
//
//		return 0;
//
//	}
	
	/**
	 * Gives all the saved chunks id's of this backup file
	 * @param fileID
	 * @return
	 */
//	public ArrayList<Pair<Pair<String, Integer>, Integer>> getAllChunks(String fileID) {
//		ArrayList<Pair<Pair<String, Integer>, Integer>> result = new ArrayList<Pair<Pair<String, Integer>, Integer>>();
//		
//		for(ConcurrentHashMap.Entry<Integer, Chunk> entry : chunks.entrySet()) {
//			if(isStoringChunk(entry.getKey())) {
//				Pair<String, Integer> file_chunk = new Pair<String, Integer>(fileID, entry.getKey());
//				Pair<Pair<String, Integer>, Integer> file_chunk_size = new Pair<Pair<String, Integer>, Integer> (file_chunk, entry.getValue().getexceededAmount());
//				result.add(file_chunk_size);
//			}
//		}
//		
//		return result;
//	}
	
	public boolean isBackupInitiator() {
		return pathName != null;
	}

	/**
	 * Mark that this file was deleted
	 */
	public void notifyItWasDeleted() {
		wasDeleted = true;
	}

	public boolean isReplicationDegreeZero() {
		for (Entry<Integer, Chunk> entry : chunks.entrySet()) {
			if(!entry.getValue().isReplicationDegreeZero()) {
				return false;
			}
		}
		return true;
	}

	public boolean checkIfIHaveAllChunks() {
		for (Entry<Integer, Chunk> entry : chunks.entrySet()) {
			if(entry.getValue().getRestoreMode() == State.RECEIVE) {
				return false;
			}
		}
		return true;
	}
	
//	public boolean isStoringChunk(int chunkID) {
//		return chunks.get(chunkID).isStoringChunk();
//	}

	

}
