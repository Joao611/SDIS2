package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import chord.ChordManager;
import utils.*;

public class Server implements Runnable {

	public static final int MAX_LENGTH_PACKET = 300;

	private ArrayList<String> cipher_list;
	private int port_number;
	private ChordManager chordManager;
	
	public Server(String[] cipher_suite, int port, ChordManager p) throws Exception {
		chordManager = p;
		port_number = port;
		
		System.setProperty("javax.net.ssl.keyStore","server.keys");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		System.setProperty("javax.net.ssl.trustStore","truststore");
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		cipher_list = new ArrayList<String>();
		for(int i = 1; i < cipher_suite.length; i++) {
	    	cipher_list.add(cipher_suite[i]);
	    }
	}

	@Override
	public void run() {
		SSLServerSocketFactory serverfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket server;
		try {
			server = (SSLServerSocket) serverfactory.createServerSocket(port_number);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		server.setNeedClientAuth(true);
		server.setEnabledProtocols(server.getSupportedProtocols());
		cipher_list.toArray(new String[0]);
		while (true) {
			SSLSocket socket;
			try {
				socket = (SSLSocket) server.accept();
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
			InputStream in;
			try {
				in = socket.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			byte[] in_data = new byte[1024];
			try {
				in.read(in_data);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			String response = parseMessage(in_data);
			sendResponse(socket,response);
			
		}

	}
	
	/**
	 * 
	 * @param socket
	 * @param response
	 */
	private void sendResponse(SSLSocket socket, String response) {
		OutputStream out;
		try {
			out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		byte[] out_data = response.getBytes();
		try {
			out.write(out_data);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * 
	 * @param in_data
	 * @return
	 */
	private String parseMessage(byte[] in_data) {
		String request = new String(in_data);
		System.out.println("SSLServer: "+ request);
		request = request.trim();
		String[] elements = request.split(" ");
		String response = new String();
		
//		TODO methods
		System.out.println("Elements :" + elements[0]);
		System.out.println("Elements :" + elements[1]);
		return chordManager.lookup(new UnsignedByte(Short.valueOf((elements[1]))));
	}

}