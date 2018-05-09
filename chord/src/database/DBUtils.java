package database;

import java.sql.*;

import chord.PeerInfo;
import utils.Utils;

public class DBUtils {
	
	private static final String insertFileStored = "INSERT INTO FILESSTORED "
			+ "(file_id, i_am_responsible, peer_requesting) VALUES (?,?,?)";
//	private static final String insertPeer = "INSERT INTO PEERS "
//			+ "(peer_id,ip,port) VALUES (?,?,?)";
	private static final String insertPeer = "INSERT INTO PEERS "
			+ "(peer_id,ip,port) VALUES (?,?,?) "
			+ "WHERE NOT EXISTS (SELECT * FROM peers WHERE peer_id = ?)";
	
	
	
	private static final String insertChunkStored = "INSERT INTO CHUNKSSTORED "
			+ "(chunk_id,file_id) VALUES (?,?)";
	private static final String insertBackupRequested = "INSERT INTO BACKUPSREQUESTED "
			+ "(file_id, filename, encrypt_key) VALUES (?,?,?)";
	private static final String getFileById = "SELECT * FROM FILESSTORED "
			+ "WHERE file_id = ?";

	public static void insertStoredFile(Connection conn, FileStoredInfo fileInfo) {
		String peerRequesting = fileInfo.getPeerRequesting();
		try {
			PreparedStatement p = conn.prepareStatement(insertFileStored);
			p.setString(1, fileInfo.getFileId());
			p.setBoolean(2, fileInfo.getiAmResponsible());
			if (peerRequesting == null) {
				p.setNull(3, Types.VARCHAR);
			} else {
				p.setString(3, peerRequesting);
			}
			p.executeUpdate();
			Utils.log("File " + fileInfo.getFileId() + " has been stored");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void insertPeer(Connection conn, PeerInfo peerInfo) {
		try {
			PreparedStatement p = conn.prepareStatement(insertPeer);
			p.setString(1, peerInfo.getId());
			p.setString(4, peerInfo.getId());
			p.setString(2, peerInfo.getAddr().getHostAddress());
			p.setInt(3, peerInfo.getPort());
			p.executeUpdate();
			Utils.log("Peer " + peerInfo.getId() + " has been stored");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void insertStoredChunk(Connection conn, ChunkInfo chunkInfo) {
		try {
			PreparedStatement p = conn.prepareStatement(insertChunkStored);
			p.setInt(1, chunkInfo.getChunkId());
			p.setString(2, chunkInfo.getFileId());
			p.executeUpdate();
			Utils.log("Chunk " + chunkInfo.getChunkId() + " has been stored");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void insertBackupRequested(Connection conn, BackupRequest backupRequest) {
		try {
			PreparedStatement p = conn.prepareStatement(insertBackupRequested);
			p.setString(1, backupRequest.getFileId());
			p.setString(2, backupRequest.getFilename());
			if (backupRequest.getEncryptKey() != null) {
				p.setString(3, backupRequest.getEncryptKey());
			}else {
				p.setNull(3, Types.VARCHAR);
			}
			p.executeUpdate();
			Utils.log("BackupRequest for file " + backupRequest.getFilename() + " has been stored");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean amIResponsible(Connection conn, String fileId) {
		try {
			PreparedStatement p = conn.prepareStatement(getFileById);
			p.setString(1, fileId);
			ResultSet result = p.executeQuery();
			if (result.next()) {
				return result.getBoolean("i_am_responsible");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
