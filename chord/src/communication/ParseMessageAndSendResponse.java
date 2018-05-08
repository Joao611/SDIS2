/**
 * 
 */
package communication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.net.ssl.SSLSocket;

import chord.ChordManager;
import chord.PeerInfo;
import messages.MessageFactory;
import messages.MessageType;
import program.Peer;
import state_info.BackupFile;
import state_info.LocalState;
import utils.UnsignedByte;
import utils.Utils;

/**
 * @author anabela
 *
 */
public class ParseMessageAndSendResponse implements Runnable {

	private byte[] readData;
	private SSLSocket socket;
	private Server server;
	private Peer peer;


	public ParseMessageAndSendResponse(Server server, Peer peer, byte[] readData, SSLSocket socket) {
		super();
		this.readData = readData;
		this.socket = socket;
		this.peer = peer;
		this.server = server;
	}


	@Override
	public void run() {
		String response = parseMessage(readData);

		sendResponse(socket, response);

	}

	/**
	 * Parses the received request, processes it and returns the protocol response
	 * @param readData
	 * @return
	 */
	String parseMessage(byte[] readData) {
		String request = new String(readData);
		Utils.LOGGER.finest("SSLServer: " + request);

		request = request.trim();
		String[] lines = request.split("\r\n");
		String[] firstLine = lines[0].split(" ");
		String[] secondLine = null;
		if (lines.length > 1) {
			secondLine = lines[1].split(" ");
		}
		String response = new String();

		switch (MessageType.valueOf(firstLine[0])) {
		case LOOKUP:
			if (secondLine != null) {
				response = peer.getChordManager().lookup(new UnsignedByte(Short.valueOf((secondLine[0]))));
			}else {
				Utils.LOGGER.info("Invalid lookup message");
			}
			break;
		case PING:
			response = MessageFactory.getHeader(MessageType.OK, "1.0", peer.getChordManager().getPeerInfo().getId());
			break;
		case NOTIFY:
			peer.getChordManager().setPredecessor(parseNotifyMsg(firstLine,secondLine));
			response = MessageFactory.getHeader(MessageType.OK, "1.0", peer.getChordManager().getPeerInfo().getId());
			break;
		case PUTCHUNK:
			break;
		case STABILIZE:
			response = MessageFactory.getFirstLine(MessageType.PREDECESSOR, "1.0", peer.getChordManager().getPeerInfo().getId());
			response = MessageFactory.appendLine(response, peer.getChordManager().getPredecessor().asArray());
			System.err.println(response);
			break;
		case STORED: {
			response = parseStoredMsg(secondLine);
			break;
		}
		default:
			break;
		}
		return response;
	}

	private String parseStoredMsg(String[] lines) {
		String fileID = lines[0];
		Integer chunkNo = Integer.valueOf(lines[1]);
		Integer repDegree = Integer.valueOf(lines[2]);

		PreparedStatement preparedStatement;
		ResultSet result;
		try {
			preparedStatement = peer.getChordManager().getDatabase().getConnection().prepareStatement("SELECT * FROM filesstored WHERE id = ?;");
			preparedStatement.setString(1, fileID); //STARTS AT 1 FOR SOME REASON
			result = preparedStatement.executeQuery();
			if(result.first()) { //Exists
				int id = result.getInt("id");
				boolean i_am_responsible = result.getBoolean("i_am_responsible");
				int desired_rep_degree = result.getInt("desired_rep_degree");
				int actual_rep_degree = result.getInt("actual_rep_degree");
				int peer_which_requested = result.getInt("peer_which_requested");
				
				repDegree++; // I am also storing the chunk
				
				PreparedStatement p = peer.getChordManager().getDatabase().getConnection().prepareStatement("UPDATE filesstored SET actual_rep_degree = ? WHERE id = ?;");
				p.setInt(1, repDegree);
				p.setString(2, fileID);
				p.executeUpdate();
				
				if(i_am_responsible) {
					//TODO: send response to requesting peer
					String message = MessageFactory.getConfirmStored(peer.getChordManager().getPeerInfo().getId(), fileID, chunkNo, repDegree);
					PreparedStatement k = peer.getChordManager().getDatabase().getConnection().prepareStatement("SELECT * FROM peer WHERE id = ?;");
					k.setInt(1, peer_which_requested);
					ResultSet peerRequest = k.executeQuery();
					if(peerRequest.first()) {
						InetAddress addr = InetAddress.getByName(peerRequest.getString("ip"));
						int port = peerRequest.getInt("port");
						Client.sendMessage(addr, port, message, false);
					} else {
						Utils.LOGGER.severe("ERROR: could not get peer whitch requested!");
						return null;
					}
					return null;
				}
			}
			result.close();
		} catch (SQLException | UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		String message = MessageFactory.getStored(peer.getChordManager().getPeerInfo().getId(), fileID, chunkNo, repDegree);
		Client.sendMessage(peer.getChordManager().getPredecessor().getAddr(),peer.getChordManager().getPredecessor().getPort(), message, false);
		
		return null;
	}


	private PeerInfo parseNotifyMsg(String[] firstLine, String[] secondLine) {
		UnsignedByte id = new UnsignedByte(Short.parseShort(firstLine[2]));
		InetAddress addr = socket.getInetAddress();
		int port = Integer.parseInt(secondLine[0].trim());
		return new PeerInfo(id, addr, port);
	}

	/**
	 * 
	 * @param socket
	 * @param response
	 */
	void sendResponse(SSLSocket socket, String response) {
		OutputStream sendStream;
		try {
			sendStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		byte[] sendData = response.getBytes();
		try {
			sendStream.write(sendData);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
