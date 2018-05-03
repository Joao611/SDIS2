package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import chord.AbstractPeerInfo;
import chord.ChordManager;
import utils.UnsignedByte;

public class Server implements Runnable {

	public static final int MAX_LENGTH_PACKET = 300;

	private ArrayList<String> cipher_list;
	private int port_number;
	private ChordManager chordManager;
	private ThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
	
	public Server(String[] cipher_suite, int port, ChordManager chordManager) throws Exception {
		this.chordManager = chordManager;
		this.port_number = port;

		setSystemProperties();

		this.cipher_list = new ArrayList<String>();

		for (int i = 1; i < cipher_suite.length; i++) {
			this.cipher_list.add(cipher_suite[i]);
		}
	}

	@Override
	public void run() {
		SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket serverSocket;
		try {
			serverSocket = (SSLServerSocket) serverFactory.createServerSocket(this.port_number);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		serverSocket.setNeedClientAuth(true);
		serverSocket.setEnabledProtocols(serverSocket.getSupportedProtocols());

		this.cipher_list.toArray(new String[0]);

		while (true) {
			SSLSocket socket;
			try {
				socket = (SSLSocket) serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			try {
				socket.startHandshake();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}

			byte[] readData = readSocket(socket);

			ParseMessageAndSendResponse p = new ParseMessageAndSendResponse(this,readData, socket);
			
			threadPool.execute(p);	
		}

	}

	public void setSystemProperties() {
		System.setProperty("javax.net.ssl.keyStore", "server.keys");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		System.setProperty("javax.net.ssl.trustStore", "truststore");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");
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

		if (elements[0].equals("lookup")) {
			response = chordManager.lookup(new UnsignedByte(Short.valueOf((elements[1]))));
		} else if (elements[0].equals("stabilize")) {
			AbstractPeerInfo predecessor = this.chordManager.getPredecessor();
			response = "Predecessor " + predecessor.toString();
		} else if (elements[0].equals("status")) {
			response = "OK";
		}

		return response;
	}

	/**
	 * Read socket
	 */
	public byte[] readSocket(SSLSocket socket) {
		InputStream readStream;
		try {
			readStream = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		byte[] readData = new byte[1024];
		try {
			readStream.read(readData);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return readData;
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