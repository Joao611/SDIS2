package communication;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import utils.UnsignedByte;

public class Client {
	private static ArrayList<String> cypher = new ArrayList<String>(Arrays.asList("TLS_DHE_RSA_WITH_AES_128_CBC_SHA"));

	public static String message(InetAddress addr, int port, String message) {

		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket;
		try {
			socket = (SSLSocket) factory.createSocket(addr, port);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		socket.setEnabledCipherSuites(cypher.toArray(new String[0]));

		byte[] out_data = message.getBytes();

		OutputStream out;
		try {
			out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			out.write(out_data);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return getResponse(socket);
	}

	public static String getResponse(SSLSocket socket) {
		byte[] in_data = new byte[1024];
		InputStream in;
		try {
			in = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			in.read(in_data);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return new String(in_data);

	}


}

