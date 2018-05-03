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
import messages.MessageType;
import program.Peer;
import utils.UnsignedByte;

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
		System.out.println("SSLServer: " + request);

		request = request.trim();
		String[] elements = request.split(" ");
		String response = new String();

		for (String element : elements) {
			System.out.println(element);
		}

		switch (MessageType.valueOf(elements[0])) {
		case LOOKUP:
			response = chordManager.lookup(new UnsignedByte(Short.valueOf((elements[1]))));
			break;
		case PING:
			response = "OK";
			break;
		case NOTIFY:
			chordManager.setPredecessor(parseNotifyMsg(elements));
			response = "OK";
			break;
		case ASK:
			break;
		case OK:
			break;
		case PUTCHUNK:
			break;
		case SUCCESSOR:
			break;
		default:
			break;
		}
		
		return response;
	}

	private PeerInfo parseNotifyMsg(String[] elements) {
		UnsignedByte id = new UnsignedByte(Short.parseShort(elements[2]));
		InetAddress addr = socket.getInetAddress();
		int port = socket.getPort();
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
