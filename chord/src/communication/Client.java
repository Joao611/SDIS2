package communication;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import utils.Utils;

public class Client {
	private static ArrayList<String> cipher = new ArrayList<String>(Arrays.asList("TLS_DHE_RSA_WITH_AES_128_CBC_SHA"));


	public static String sendMessage(InetAddress addr, int port, String message, boolean waitForResponse) {

		String response = null;
		SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();


		//      AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
		SSLSocket socket;
		try {
			socket = (SSLSocket) socketFactory.createSocket(addr, port);
			socket.setSoTimeout(25000);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		socket.setEnabledCipherSuites(cipher.toArray(new String[0]));

		send(message, socket);

		if(waitForResponse) {
			response = getResponse(socket);//fica bloqueado a espera de resposta
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * Write to the socket (send message)
	 */
	public static void send(String message, SSLSocket socket) {
		byte[] sendData = message.getBytes(StandardCharsets.ISO_8859_1);
		sendData = encode(sendData);
		OutputStream sendStream;
		try {
			sendStream = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			sendStream.write(sendData);
			sendStream.write('\t');
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	private static byte[] encode(byte[] sendData) {
		ArrayList<Byte> res = new ArrayList<Byte>();
		for(int i = 0; i < sendData.length; i++) {
			if(sendData[i]=='\t') {
				res.add((byte) '\f');
				res.add((byte) '\t');
			} else {
				if(sendData[i]=='\f') {
					res.add((byte) '\f');
					res.add((byte) '\f');
				
				}else {
					res.add(sendData[i]);
				}
			}
		}
		byte[] a = new byte[res.size()];
		for (int i = 0; i < res.size(); i++) {
			a[i] = res.get(i);
		}
		return a;
	}

	/**
	 * Receives the message's response
	 */
	public static String getResponse(SSLSocket socket) {
		byte[] readData = new byte[1024 + Utils.MAX_LENGTH_CHUNK];
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

