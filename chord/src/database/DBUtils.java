package database;

import java.sql.*;

import chord.PeerInfo;
import utils.Utils;

public class DBUtils {
	
	private static final String insertFileStored = "INSERT INTO FILESSTORED "
			+ "(id, i_am_responsible, peer_requesting) VALUES (?,?,?)";
	private static final String insertPeer = "INSERT INTO PEERS "
			+ "(id,ip,port) VALUES (?,?,?)";
	private static final String insertChunkStored = "INSERT INTO CHUNKSSTORED "
			+ "(id,file_id) VALUES (?,?)";
	private static final String getFileById = "SELECT * FROM FILESSTORED "
			+ "WHERE id=?";

	public static void insertStoredFile(Connection conn, FileStoredInfo fileInfo) {
		Short peerRequesting = fileInfo.getPeerWhichRequested();
		try {
			PreparedStatement p = conn.prepareStatement(insertFileStored);
			p.setString(1, fileInfo.getFileId());
			p.setBoolean(2, fileInfo.getiAmResponsible());
			if (peerRequesting == null) {
				p.setNull(3, Types.INTEGER);
			} else {
				p.setInt(3, peerRequesting);
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
			p.setInt(1, peerInfo.getId());
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
			p.setInt(2, chunkInfo.getFileId());
			p.executeUpdate();
			Utils.log("Chunk " + chunkInfo.getChunkId() + " has been stored");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean amIResponsible(Connection conn, Integer fileId) {
		try {
			PreparedStatement p = conn.prepareStatement(getFileById);
			p.setInt(1, fileId);
			ResultSet result = p.executeQuery();
			if (result.first()) {
				return result.getBoolean("i_am_responsible");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
