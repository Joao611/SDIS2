package communication;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import messages.MessageFactory;

public class Client {
	private static ArrayList<String> cipher = new ArrayList<String>(Arrays.asList("TLS_DHE_RSA_WITH_AES_128_CBC_SHA"));
	private static final String ERROR_MESSAGE = MessageFactory.getErrorMessage();
	
	public static String sendMessage(InetAddress addr, int port, String message, boolean waitForResponse) {

		String response = null;
		SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		
		SSLSocket socket;
		try {
			socket = (SSLSocket) socketFactory.createSocket(addr, port);
			socket.setSoTimeout(5000);
		} catch (IOException e) {
			System.err.println("Connection refused contacting port " + port);
			return ERROR_MESSAGE;
		}

		socket.setEnabledCipherSuites(cipher.toArray(new String[0]));

		try {
			send(message, socket);
		} catch (IOException e1) {
			System.err.println("Connection refused");
			return ERROR_MESSAGE;
		}
		if(waitForResponse) {
			response = getResponse(socket);//fica bloqueado a espera de resposta
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error closing connection");
			return ERROR_MESSAGE;
		}
		return response;
	}

	/**
	 * Write to the socket (send message)
	 * @throws IOException 
	 */
	public static void send(String message, SSLSocket socket) throws IOException {
		byte[] sendData = message.getBytes(StandardCharsets.ISO_8859_1);
		OutputStream sendStream = socket.getOutputStream();
		sendStream.write(sendData);
	}

	/**
	 * Receives the message's response
	 */
	public static String getResponse(SSLSocket socket) {
		byte[] readData = new byte[1024];
		InputStream readStream;
		try {
			readStream = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			readStream.read(readData);
		} catch(SocketTimeoutException e) {
			System.err.println("Socket timeout");
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new String(readData,StandardCharsets.ISO_8859_1);

	}


}

