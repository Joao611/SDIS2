package chord;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
  private InetAddress addr;
private int port_number;
private ArrayList<String> cypher;
private UnsignedByte unsignedByte;

public Client(String[] args,UnsignedByte unsignedByte) throws Exception {
this.unsignedByte= unsignedByte;
	if (args.length < 3) {
      System.out.println("Usage: java SSLClient <host> <port> <cypher-suite>*");
      return;
    }
    
   
    System.setProperty("javax.net.ssl.keyStore","client.keys");
	System.setProperty("javax.net.ssl.keyStorePassword","123456");
	System.setProperty("javax.net.ssl.trustStore","truststore");
	System.setProperty("javax.net.ssl.trustStorePassword","123456");

    addr = InetAddress.getByName(args[0]);
    port_number = Integer.parseInt(args[1]);
    cypher = new ArrayList<String>();
    int i = 2;
    for(; i < args.length; i++) {
    	cypher.add(args[i]);
    }
      }

public String run() {
	//Criar Request
    String request = new String();
    request = "lookup "+unsignedByte.getB();
	SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    SSLSocket socket;
	try {
		socket = (SSLSocket) factory.createSocket(addr, port_number);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
    socket.setEnabledCipherSuites(cypher.toArray(new String[0]));
   
    byte[] out_data = request.getBytes();
    
    OutputStream out;
	try {
		out = socket.getOutputStream();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
    try {
		out.write(out_data);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
    System.out.println("PACKET SENT");
    
    byte[] in_data = new byte[1024];
    InputStream in;
	try {
		in = socket.getInputStream();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
    try {
		in.read(in_data);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return null;
	}
    
    System.out.println("Recebi reposta");
    System.out.println(new String(in_data));
    
    System.out.println("SSLClient: "+ request +" : "+ new String(in_data));

	return new String(in_data);
}
}