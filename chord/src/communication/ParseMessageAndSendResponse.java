/**
 * 
 */
package communication;

import javax.net.ssl.SSLSocket;

/**
 * @author anabela
 *
 */
public class ParseMessageAndSendResponse implements Runnable {

	private byte[] readData;
	private SSLSocket socket;
	private Server server;

	
	public ParseMessageAndSendResponse(Server server, byte[] readData, SSLSocket socket) {
		super();
		this.readData = readData;
		this.socket = socket;
		this.server = server;
	}


	@Override
	public void run() {
		String response = server.parseMessage(readData);

		server.sendResponse(socket, response);

	}

}
