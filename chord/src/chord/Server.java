package chord;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server implements Runnable{

	public static final int MAX_LENGTH_PACKET = 300;

	private ArrayList<String> cypher;
	private int port_number;
	private PeerInChord peer;
	public Server(String[] args, PeerInChord p) throws Exception {
		peer = p;
		if (args.length < 1) {
			System.out.println("Usage: java SSLServer <port> <cypher-suite>*");
			return;
		}
		
		System.setProperty("javax.net.ssl.keyStore","server.keys");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		System.setProperty("javax.net.ssl.trustStore","truststore");
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		//System.setProperty("javax.net.debug","all");
		
		port_number = Integer.parseInt(args[0]);
		cypher = new ArrayList<String>();
		for(int i = 1; i < args.length; i++) {
	    	cypher.add(args[i]);
	    }
			}

	@Override
	public void run() {
		HashMap<String, String> map = new HashMap<String, String>();
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
		cypher.toArray(new String[0]);
		while (true) {
			SSLSocket socket;
			try {
				socket = (SSLSocket) server.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			try {
				socket.startHandshake();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			InputStream in;
			try {
				in = socket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			byte[] in_data = new byte[1024];
			try {
				in.read(in_data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			String request = new String(in_data);
			System.out.println("SSLServer: "+ request);
			request = request.trim();
			String[] elements = request.split(" ");
			String response = new String();
			
//			TODO methids
			System.out.println("Elements :" + elements[0]);
			System.out.println("Elements :" + elements[1]);
			response = peer.lookup((elements[1].charAt(0)));
			System.out.println(response);
			OutputStream out;
			try {
				out = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			byte[] out_data = response.getBytes();
			System.out.println(response);
			try {
				out.write(out_data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}

	}

}