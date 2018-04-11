package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SSLServer {
	
	// (plate number, owner name)
	private HashMap<String,String> storedData = new HashMap<String,String>();
	
	private SSLServerSocket serverSocket;
	private int port;
	private ArrayList<String> wantedCipherSuites;
	
	public SSLServer(String[] args) {
		parseArgs(args);
		initServerSocket();
	}

	private void parseArgs(String[] args) {
		if (args.length < 1) {
			printUsage();
		}
		port = Integer.parseInt(args[0]);
		for (int i = 1; i < args.length; i++) {
			wantedCipherSuites.add(args[i]);
		}
	}

	private void initServerSocket() {
		SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();  
		 
		try {  
			serverSocket = (SSLServerSocket) ssf.createServerSocket(port);  
		}  
		catch(IOException e) {  
		    System.out.println("Server - Failed to create SSLServerSocket");  
		    e.getMessage();  
		    return;  
		}
		
		serverSocket.setNeedClientAuth(true);
		
		//TODO: set wanted cipher suites
		initServerCipherSuites();
	}

	private void initServerCipherSuites() {
		String[] supportedSuites = serverSocket.getSupportedCipherSuites();
		ArrayList<String> wantedSupportedSuites = new ArrayList<String>();
		for (String wantedSuite : wantedCipherSuites) {
			if (Arrays.asList(supportedSuites).contains(wantedSuite)) {
				wantedSupportedSuites.add(wantedSuite);
			}
		}
		
		serverSocket.setEnabledCipherSuites(wantedSupportedSuites.toArray(new String[wantedSupportedSuites.size()]));
	}

	private void printUsage() {
		System.out.println("java SSLServer <port> <cypher-suite>*");
	}

	private void run() {
		while (true) {
			serverIteration();
		}
	}


	private void serverIteration() {
		SSLSocket socket = null;
		try {
			socket = (SSLSocket) serverSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			// Create Input / Output Streams for communication with the client
			while (true) {
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		        BufferedReader in = new BufferedReader(
						new InputStreamReader(
								socket.getInputStream()));
		        String inputLine, outputLine;

		        while ((inputLine = in.readLine()) != null) {
		             out.println(inputLine);
		             System.out.println(inputLine);
		        }
		        
		        // Close the streams and the socket
		        out.close();
		        in.close();
		        socket.close();
			}
		} catch (Exception exp) {
			PrivilegedActionException priexp = new PrivilegedActionException(exp);
			System.out.println(" Priv exp --- " + priexp.getMessage());

			System.out.println(" Exception occurred .... " +exp);
			exp.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SSLServer server = new SSLServer(args);
		server.run();
	}
}
