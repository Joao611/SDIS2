/**
 * 
 */
package program;

import java.sql.Timestamp;
import java.util.ArrayList;

import database.ChunkInfo;
import database.DBUtils;
import utils.Utils;

/**
 * @author anabela
 *
 */
public class Leases implements Runnable {

	
	Peer peer;
	
	public Leases(Peer peer) {
		super();
		this.peer = peer;
	}

	@Override
	public void run() {
		Timestamp time = new Timestamp(System.currentTimeMillis());
		ArrayList<String> filesToDelete = DBUtils.getFilesToDelete(peer.getConnection(), time);
		
		String msg = new String();
		for(int i = 0; i < filesToDelete.size(); i++) {
			msg += filesToDelete.get(i) + "\n";
			ArrayList<ChunkInfo> allChunks = DBUtils.getAllChunksOfFile(peer.getConnection(), filesToDelete.get(i));
			allChunks.forEach(chunk -> {
				Utils.deleteFile(Peer.getPath().resolve(chunk.getFilename()));
				Peer.decreaseStorageUsed(chunk.getSize());
			});
			DBUtils.deleteFile(peer.getConnection(), filesToDelete.get(i));
			System.out.println("Deleted file: " + filesToDelete.get(i));
		}
		
		System.err.println(msg);
	}

}
