package database;

import java.sql.*;

import utils.Utils;

public class DBUtils {
	
	private static final String insertFileStored = "INSERT INTO FILESSTORED "
			+ "(id, i_am_responsible, peer_requesting) VALUES (?,?,?)";

	public static void saveStoredFile(Connection conn, FileStoredInfo fileInfo) {
		Integer peerRequesting = fileInfo.getPeerWhichRequested();
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
			Utils.log("File has been stored");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
