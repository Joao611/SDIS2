package src;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServer {

	public static final int MAX_LENGTH_PACKET = 300;

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Usage: java SSLServer <port> <cypher-suite>*");
			return;
		}
		
		System.setProperty("javax.net.ssl.keyStore","server.keys");
		System.setProperty("javax.net.ssl.keyStorePassword","123456");
		System.setProperty("javax.net.ssl.trustStore","truststore");
		System.setProperty("javax.net.ssl.trustStorePassword","123456");
		
		//System.setProperty("javax.net.debug","all");
		
		int port_number = Integer.parseInt(args[0]);
		ArrayList<String> cypher = new ArrayList<String>();
		for(int i = 1; i < args.length; i++) {
	    	cypher.add(args[i]);
	    }
		HashMap<String, String> map = new HashMap<String, String>();
		SSLServerSocketFactory serverfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket server = (SSLServerSocket) serverfactory.createServerSocket(port_number);
		server.setNeedClientAuth(true);
		server.setEnabledProtocols(server.getSupportedProtocols());
		cypher.toArray(new String[0]);
		while (true) {
			SSLSocket socket = (SSLSocket) server.accept();
			socket.startHandshake();
			InputStream in = socket.getInputStream();
			byte[] in_data = new byte[1024];
			in.read(in_data);
			String request = new String(in_data);
			System.out.println("SSLServer: "+ request);
			request = request.trim();
			String[] elements = request.split(" ");
			String response = new String();
			if ("register".equals(elements[0])) {
				if (map.containsKey(elements[1])) {
					System.out.println("Existe");
					response += -1;
				} else {
					System.out.println("vou criar");
					String name = new String();
					for (int i = 2; i < elements.length; i++) {
						name += elements[i] + ' ';
					}
					map.put(elements[1], name);
					response += Integer.toString(map.size());
				}
			} else if ("lookup".equals(elements[0])) {
				String name = map.get(elements[1]);
				response += name;
			} else {
				System.out.println("Not valid request!");
				response += "ERROR";
				break;
			}

			OutputStream out = socket.getOutputStream();
			byte[] out_data = response.getBytes();
			System.out.println(response);
			out.write(out_data);
		}
	}

}