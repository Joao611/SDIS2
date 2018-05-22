/**
 * 
 */
package program;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import communication.Client;
import database.BackupRequest;
import database.ChunkInfo;
import database.DBUtils;
import messages.MessageFactory;
import utils.Utils;

/**
 * @author anabela
 *
 */
public class Leases implements Runnable {


	public static final int HALF_LEASE_TIME = 1;
	public static final int LEASE_TIME = 2*HALF_LEASE_TIME;
	public static final TimeUnit LEASE_UNIT = TimeUnit.MINUTES;
	Peer peer;

	public Leases(Peer peer) {
		super();
		this.peer = peer;
	}

	@Override
	public void run() {
		Timestamp time = new Timestamp(System.currentTimeMillis());

		deleteFiles(time);
		updateFiles(time);

	}

	private void updateFiles(Timestamp time) {
		ArrayList<BackupRequest> filesToUpdate = DBUtils.getFilesToUpdate(peer.getConnection());

		for(int i = 0; i < filesToUpdate.size(); i++) {
			
			peer.backup(filesToUpdate.get(i).getFilename(),
					filesToUpdate.get(i).getDesiredRepDegree(),
					filesToUpdate.get(i).getEncryptKey());
			Utils.LOGGER.info("Lease:Updated file: " + filesToUpdate.get(i));
		}

	}

	private void deleteFiles(Timestamp time) {
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
			Utils.LOGGER.info("Lease:Deleted file: " + filesToDelete.get(i));
		}
	}

}
