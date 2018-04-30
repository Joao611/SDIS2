package chord;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
  public Client(String[] args) throws Exception {
    if (args.length < 3) {
      System.out.println("Usage: java SSLClient <host> <port> <cypher-suite>*");
      return;
    }
    //Criar Request
    String request = new String();
    request = "lookup 0";
    System.setProperty("javax.net.ssl.keyStore","client.keys");
	System.setProperty("javax.net.ssl.keyStorePassword","123456");
	System.setProperty("javax.net.ssl.trustStore","truststore");
	System.setProperty("javax.net.ssl.trustStorePassword","123456");

    InetAddress addr = InetAddress.getByName(args[0]);
    int port_number = Integer.parseInt(args[1]);
    ArrayList<String> cypher = new ArrayList<String>();
    int i = 2;
    for(; i < args.length; i++) {
    	cypher.add(args[i]);
    }
    SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    SSLSocket socket = (SSLSocket) factory.createSocket(addr, port_number);
    socket.setEnabledCipherSuites(cypher.toArray(new String[0]));
   
    byte[] out_data = request.getBytes();
    
    OutputStream out = socket.getOutputStream();
    out.write(out_data);
    System.out.println("PACKET SENT");
    
    byte[] in_data = new byte[1024];
    InputStream in = socket.getInputStream();
    in.read(in_data);
    
    System.out.println("Recebi reposta");
    System.out.println(new String(in_data));
    
    System.out.println("SSLClient: "+ request +" : "+ new String(in_data));
  }
}