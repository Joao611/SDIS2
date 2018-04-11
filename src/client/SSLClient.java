package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {
	
	private InetAddress host;
	private int port;
	private String operation;
	private String plateNumber;
	private String ownerName;
	private ArrayList<String> wantedCipherSuites = new ArrayList<String>();
	
	SSLSocket socket;
	PrintWriter out = null;
    BufferedReader in = null;

	public SSLClient(String[] args) throws IOException {
		parseArgs(args);
		initSocket();
	}

	private void initSocket() throws IOException {
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory)SSLSocketFactory.getDefault();
		socket = (SSLSocket) sslsocketfactory.createSocket(host, port);
		
		// Initializing the streams for Communication with the Server
     	out = new PrintWriter(socket.getOutputStream(), true);
     	in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	private void parseArgs(String[] args) throws UnknownHostException {
		host = InetAddress.getByName(args[0]);
		port = Integer.parseInt(args[1]);
		operation = args[2];
		plateNumber = args[3];
		int cipherSuitesFirstInd = 4;
		if (operation.equals("REGISTER")) {
			ownerName = args[4];
			cipherSuitesFirstInd = 5;
		}
		
		for (int i = cipherSuitesFirstInd; i < args.length; i++) {
			wantedCipherSuites.add(args[i]);
		}
	}

	public static void main(String[] args) throws IOException {
		SSLClient client = null;
		try {
			client = new SSLClient(args);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.run();
	}

	private void run() {
		switch (operation) {
		case "REGISTER":
			sendRegister();
			break;
		case "LOOKUP":
			sendLookup();
			break;
		default:
			System.err.println("Invalid operation.");
		}
		
		receiveReply();
	}

	private void sendRegister() {
		// TODO Auto-generated method stub
		
	}
}
