package communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import chord.ChordManager;
import utils.SingletonThreadPoolExecutor;

public class Server implements Runnable {

	public static final int MAX_LENGTH_PACKET = 300;

	private ArrayList<String> cipher_list;
	private int port_number;
	private ChordManager chordManager;
	
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

			ParseMessageAndSendResponse p = new ParseMessageAndSendResponse(this, chordManager, readData, socket);
			
			SingletonThreadPoolExecutor.getInstance().get().execute(p);	
		}

	}

	public void setSystemProperties() {
		System.setProperty("javax.net.ssl.keyStore", "server.keys");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		System.setProperty("javax.net.ssl.trustStore", "truststore");
		System.setProperty("javax.net.ssl.trustStorePassword", "123456");
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


}