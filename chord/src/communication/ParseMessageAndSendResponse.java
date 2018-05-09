/**
 * 
 */
package communication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.net.ssl.SSLSocket;

import chord.ChordManager;
import chord.PeerInfo;
import database.ChunkInfo;
import database.DBUtils;
import database.FileStoredInfo;
import messages.MessageFactory;
import messages.MessageType;
import program.Peer;
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
	private Connection dbConnection;


	public ParseMessageAndSendResponse(Server server, Peer peer, byte[] readData, SSLSocket socket) {
		super();
		this.readData = readData;
		this.socket = socket;
		this.peer = peer;
		this.server = server;
		this.dbConnection = peer.getChordManager().getDatabase().getConnection();
	}

	@Override
	public void run() {
		String response = parseMessage(readData);
		if (response != null) {
			sendResponse(socket, response);
		}

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
		String response = null;

		switch (MessageType.valueOf(firstLine[0])) {
		case INITDELETE:
			parseInitDelete(secondLine);
			break;
		case LOOKUP:
			if (secondLine != null) {
				response = peer.getChordManager().lookup(secondLine[0]);
			}else {
				Utils.LOGGER.warning("Invalid lookup message");
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
			parsePutChunkMsg(secondLine, thirdLine);
			break;
		case KEEPCHUNK:
			parseKeepChunkMsg(secondLine, thirdLine);
			break;
		case STABILIZE:
			response = MessageFactory.getFirstLine(MessageType.PREDECESSOR, "1.0", peer.getChordManager().getPeerInfo().getId());
			response = MessageFactory.appendLine(response, peer.getChordManager().getPredecessor().asArray());
			break;
		case STORED: {
			response = parseStoredMsg(secondLine);
			break;
		}
		case GETCHUNK: {
			response = parseGetChunkMsg(secondLine);
			break;
		}
		case CHUNK: {
			System.err.println("ESTOU A RECEBER O CHUNK");
			break;
		}
		default:
			break;
		}
		return response;
	}

	private String parseGetChunkMsg(String[] secondLine) {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(secondLine[0]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
		Integer port = Integer.valueOf(secondLine[1]);
		String fileID = secondLine[2];
		Integer chunkNo = Integer.valueOf(secondLine[3]);

		ChunkInfo chunkInfo = new ChunkInfo(chunkNo, fileID);
		if(DBUtils.checkStoredChunk(dbConnection, chunkInfo )) { //Tenho o chunk
			String body = Utils.readFile(Peer.getPath().resolve(fileID+"_"+chunkNo).toString());
			String message = MessageFactory.getChunk(this.peer.getChordManager().getPeerInfo().getId(),
					fileID, chunkNo, body.getBytes());
			Client.sendMessage(addr, port, message, false);
		} else { //ReSend GETCHUNK to successor
			String message = MessageFactory.getGetChunk(this.peer.getChordManager().getPeerInfo().getId(),
					addr, port, fileID, chunkNo);
			Client.sendMessage(this.peer.getChordManager().getSuccessor(0).getAddr(),
					this.peer.getChordManager().getSuccessor(0).getPort(), message, false);
		}
		return null;
	}

	private void parseInitDelete(String[] secondLine) {
		String fileToDelete = secondLine[0];
				
	}


	private String parseStoredMsg(String[] lines) {
		String fileID = lines[0];
		Integer chunkNo = Integer.valueOf(lines[1]);
		Integer repDegree = Integer.valueOf(lines[2]);
		
		ChunkInfo chunkInfo = new ChunkInfo(chunkNo,fileID);
		boolean chunkExists = DBUtils.checkStoredChunk(dbConnection, chunkInfo);
		if(chunkExists) { //Exists
			boolean iAmResponsible = DBUtils.amIResponsible(dbConnection, fileID);
			repDegree++; // I am also storing the chunk
			
			if(iAmResponsible) {
				chunkInfo.setActualRepDegree(repDegree);
				DBUtils.updateStoredChunkRepDegree(dbConnection, chunkInfo);
				PeerInfo peerWhichRequested = DBUtils.getPeerWhichRequestedBackup(dbConnection, fileID);
				if(peerWhichRequested != null) {
					String message = MessageFactory.getConfirmStored(peer.getChordManager().getPeerInfo().getId(), fileID, chunkNo, repDegree);
					Client.sendMessage(peerWhichRequested.getAddr(), peerWhichRequested.getPort(), message, false);
				} else {
					Utils.LOGGER.severe("ERROR: could not get peer whitch requested!");
				}
				return null;
			}
		}
		String message = MessageFactory.getStored(peer.getChordManager().getPeerInfo().getId(), fileID, chunkNo, repDegree);
		Client.sendMessage(peer.getChordManager().getPredecessor().getAddr(),peer.getChordManager().getPredecessor().getPort(), message, false);
		return null;
	}
	
	private void parsePutChunkMsg(String[] header, String body) {
		
		ChordManager chordManager = peer.getChordManager();
		byte [] body_bytes = body.getBytes();
		
		String id = header[0].trim();
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
		
		PeerInfo peerThatRequestedBackup = new PeerInfo(id,addr,port);
		DBUtils.insertPeer(dbConnection, peerThatRequestedBackup);
		FileStoredInfo fileInfo = new FileStoredInfo(id, true);
		fileInfo.setPeerRequesting(peerThatRequestedBackup.getId());
		fileInfo.setDesiredRepDegree(replicationDegree);
		DBUtils.insertStoredFile(dbConnection, fileInfo);
		
		
		if(id.equals(peer.getChordManager().getPeerInfo().getId())) {//sou o dono do ficheiro que quero fazer backup...
			//nao faz senido guardarmos um ficheiro com o chunk, visto que guardamos o ficheiro
			//enviar o KEEPCHUNK
			String message = MessageFactory.getKeepChunk(id, addr, port, fileID, chunkNo, replicationDegree, body_bytes);
			Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			return;
		}
		
		if(!Peer.capacityExceeded(body_bytes.length)) { //tem espaco para fazer o backup
			System.out.println("VOU GUARDAR");
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
			System.out.println("NAO TENHO ESPACO");
			
		}
	}
	
	private void parseKeepChunkMsg(String[] header, String body) {
		ChordManager chordManager = peer.getChordManager();
		byte [] body_bytes = body.getBytes();
		
		String id_request = header[0].trim();
		InetAddress addr_request = null;
		try {
			addr_request = InetAddress.getByName(header[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		int port_request = Integer.parseInt(header[2].trim());
		
		String fileID = header[3];
		int chunkNo = Integer.parseInt(header[4]);
		int replicationDegree = Integer.parseInt(header[5]);
		
		Path filePath = Peer.getPath().resolve(fileID + "_" + chunkNo);
		if(DBUtils.amIResponsible(dbConnection, fileID)) {//a mensagem ja deu uma volta completa. repDeg nao vai ser o desejado
			//enviar STORE para o predecessor
			System.out.println("SOU RESPONSAVEL_KEEP ");
			String message = MessageFactory.getStored(chordManager.getPeerInfo().getId(), fileID, chunkNo, 0);//porque enviar o nosso id???
			Client.sendMessage(chordManager.getPredecessor().getAddr(), chordManager.getPredecessor().getPort(), message, false);
			return;
		}
		if(id_request.equals(chordManager.getPeerInfo().getId())) {//I AM ASKING FOR THE BACKUP sou dono do ficheiro
			System.out.println("SOU DONO");
			//reencaminhar a mensagem para o proximo
			//TODO: Reencaminhar esta mal
			String message = MessageFactory.getKeepChunk(id_request, addr_request, port_request, fileID, chunkNo, replicationDegree, body_bytes); //TODO: check
			Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			return;
		}
		
		if(!Peer.capacityExceeded(body_bytes.length)) { //tem espaco para fazer o backup
			System.out.println("VOU GUARDAR");
			DBUtils.insertStoredFile(dbConnection, new FileStoredInfo(fileID, false));
			DBUtils.insertStoredChunk(dbConnection, new ChunkInfo(chunkNo,fileID));
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
				String message = MessageFactory.getKeepChunk(id_request, addr_request, port_request, fileID, chunkNo, replicationDegree - 1, body_bytes);
				Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			}
			return;
		} else {
			System.out.println("NAO ESPACO");
			//reencaminhar KEEPCHUNK para o seu sucessor
			String message = MessageFactory.getKeepChunk(id_request, addr_request, port_request, fileID, chunkNo, replicationDegree, body_bytes);
			Client.sendMessage(chordManager.getSuccessor(0).getAddr(),chordManager.getSuccessor(0).getPort(), message, false);
			return;
		}
	}


	private PeerInfo parseNotifyMsg(String[] firstLine, String[] secondLine) {
		String id = firstLine[2];
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
