/**
 * 
 */
package communication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
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
	private ChordManager chordManager;


	public ParseMessageAndSendResponse(Server server, ChordManager chordManager, byte[] readData, SSLSocket socket) {
		super();
		this.readData = readData;
		this.socket = socket;
		this.chordManager = chordManager;
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
		String thirdLine = null;//chunk body
		if (lines.length > 1) {
			secondLine = lines[1].split(" ");
		}
		if (lines.length > 2) {
			thirdLine = lines[3];
		}
		String response = new String();

		switch (MessageType.valueOf(firstLine[0])) {
		case LOOKUP:
			if (secondLine != null) {
				response = chordManager.lookup(new UnsignedByte(Short.valueOf((secondLine[0]))));
			}else {
				Utils.LOGGER.info("Invalid lookup message");
			}
			break;
		case PING:
			response = MessageFactory.getHeader(MessageType.OK, "1.0", chordManager.getPeerInfo().getId());
			break;
		case NOTIFY:
			chordManager.setPredecessor(parseNotifyMsg(firstLine,secondLine));
			response = MessageFactory.getHeader(MessageType.OK, "1.0", chordManager.getPeerInfo().getId());
			break;
		case PUTCHUNK:
			parsePutChunkMsg(secondLine, thirdLine);
			break;
		case STABILIZE:
			response = MessageFactory.getFirstLine(MessageType.PREDECESSOR, "1.0", chordManager.getPeerInfo().getId());
			response = MessageFactory.appendLine(response, chordManager.getPredecessor().asArray());
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

		BackupFile b = LocalState.getInstance().getBackupFiles().get(fileID);

		PreparedStatement preparedStatement;
		ResultSet result;
		try {
			preparedStatement = chordManager.getDatabase().getConnection().prepareStatement("SELECT * FROM filesstored WHERE id=?");
			preparedStatement.setString(1, fileID); //STARTS AT 1 FOR SOME REASON
			result = preparedStatement.executeQuery();
			if(result.first()) { //Exists
				int id = result.getInt("id");
				boolean i_am_responsible = result.getBoolean("i_am_responsible");
				int desired_rep_degree = result.getInt("desired_rep_degree");
				int actual_rep_degree = result.getInt("actual_rep_degree");
				int peer_which_requested = result.getInt("peer_which_requested");
				
				repDegree++; // I am also storing the chunk
				if(i_am_responsible) {
					//TODO: send response to requesting peer
					return null;
				}
			}
			result.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		String message = MessageFactory.getStored(chordManager.getPeerInfo().getId(), fileID, chunkNo, repDegree);
		Client.sendMessage(chordManager.getPredecessor().getAddr(),chordManager.getPredecessor().getPort(), message, false);
		
		return null;
	}
	
	private void parsePutChunkMsg(String[] header, String body) {
		byte [] body_bytes = body.getBytes();
		
		short id = Short.parseShort(header[0].trim());
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(header[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int port = Integer.parseInt(header[2].trim());
		
		String fileID = header[3];
		int chunkNo = Integer.parseInt(header[4]);
		int replicationDegree = Integer.parseInt(header[5]);
		
		Path filePath = Peer.getPath().resolve(fileID + "_" + chunkNo);
		
		//TODO: guardar na DB que sou o key owner deste fileID
		
		if(addr.equals(chordManager.getPeerInfo().getAddr())) {//sou o dono do ficheiro que quero fazer backup...
			//nao faz senido guardarmos um ficheiro com o chunk, visto que guardamos o ficheiro
			//enviar o KEEPCHUNK
			String message = MessageFactory.getKeepChunk(id, addr, port, fileID, chunkNo, replicationDegree, body_bytes);
			Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			return;
		}
		
		if(!Peer.capacityExceeded(body_bytes.length)) { //tem espaco para fazer o backup

			try {
				Utils.writeToFile(filePath, body_bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(replicationDegree == 1) {//sou o ultimo a guardar
				//enviar STORE ao que pediu o backup
				String message = MessageFactory.getStored(chordManager.getPeerInfo().getId(), fileID, chunkNo, 1);
				Client.sendMessage(addr, port, message, false);
				return;
			} else {
				//enivar KEEPCHUNK para o sucessor
				String message = MessageFactory.getKeepChunk(id, addr, port, fileID, chunkNo, replicationDegree - 1, body_bytes);
				Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			}
		} else {
			//enviar KEEPCHUNK para o seu sucessor
			String message = MessageFactory.getKeepChunk(id, addr, port, fileID, chunkNo, replicationDegree, body_bytes);
			Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
		}
	}
	
	private void parseKeepChunkMsg(String[] header, String body) {
		byte [] body_bytes = body.getBytes();
		
		short id = Short.parseShort(header[0].trim());
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(header[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int port = Integer.parseInt(header[2].trim());
		
		String fileID = header[3];
		int chunkNo = Integer.parseInt(header[4]);
		int replicationDegree = Integer.parseInt(header[5]);
		
		Path filePath = Peer.getPath().resolve(fileID + "_" + chunkNo);
		if(addr.equals(chordManager.getPeerInfo().getAddr())) {//sou dono do ficheiro
			//reencaminhar a mensagem para o proximo
			String message = MessageFactory.getKeepChunk(id, addr, port, fileID, chunkNo, replicationDegree, body_bytes);
			Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			return;
		}
		if(/* TODO: ver na DB se sou o key owner deste fileID */) {//a mensagem ja deu uma volta completa. repDeg nao vai ser o desejado
			//enviar STORE para o predecessor
			String message = MessageFactory.getStored(chordManager.getPeerInfo().getId(), fileID, chunkNo, 0);//porque enviar o nosso id???
			Client.sendMessage(chordManager.getPredecessor().getAddr(), chordManager.getPredecessor().getPort(), message, false);
			return;
		}
		if(!Peer.capacityExceeded(body_bytes.length)) { //tem espaco para fazer o backup
			//TODO: guardar na DB info do chunk
			try {
				Utils.writeToFile(filePath, body_bytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(replicationDegree == 1) {//sou o ultimo a guardar
				//enviar STORE para o predecessor
				String message = MessageFactory.getStored(chordManager.getPeerInfo().getId(), fileID, chunkNo, 1);
				Client.sendMessage(chordManager.getPredecessor().getAddr(),chordManager.getPredecessor().getPort(), message, false);
				
			} else {
				//enivar KEEPCHUNK para o sucessor
				String message = MessageFactory.getKeepChunk(id, addr, port, fileID, chunkNo, replicationDegree - 1, body_bytes);
				Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			}
			return;
		} else {
			//reencaminhar KEEPCHUNK para o seu sucessor
			String message = MessageFactory.getKeepChunk(id, addr, port, fileID, chunkNo, replicationDegree, body_bytes);
			Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			return;
		}
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
