/**
 * 
 */
package communication;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.net.ssl.SSLSocket;

import chord.ChordManager;
import chord.PeerInfo;
import messages.MessageFactory;
import messages.MessageType;
import program.Peer;
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
		Utils.log("SSLServer: " + request);

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
				response = chordManager.lookup(new UnsignedByte(Short.valueOf((secondLine[0]))));
			}else {
				System.err.println("Invalid lookup message");
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
			break;
		case STABILIZE:
			response = MessageFactory.getFirstLine(MessageType.PREDECESSOR, "1.0", chordManager.getPeerInfo().getId());
			response = MessageFactory.appendLine(response, chordManager.getPredecessor().asArray());
			System.err.println(response);
			break;
		default:
			break;
		}
		return response;
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
