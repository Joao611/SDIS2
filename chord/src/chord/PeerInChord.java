/**
 * 
 */
package chord;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author anabela
 *
 */
public class PeerInChord implements Runnable {

	private Short id;
	private InetAddress addr;
	private Integer port;
	private ArrayList<Short> fingerTable = new ArrayList<Short>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("Need a port Number");
			return;
		}
		int port = Integer.valueOf(args[0]);
		// TODO Auto-generated method stub
		PeerInChord peer = new PeerInChord(port);
		if(args.length >= 3) {
			try {
				InetAddress addrs1 = InetAddress.getByName(args[1]);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			Integer port1 = Integer.valueOf(args[2]);
			try {
				new Client(new String[] {"127.0.1.1","9000", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA" });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		peer.run();
	}

	public PeerInChord(Integer port) {
		try {
			this.addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		this.port = port;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] hash = digest.digest((this.addr.getHostAddress() + this.port).getBytes(StandardCharsets.ISO_8859_1));
		this.id = ByteBuffer.wrap(hash).getShort();
		for (int i = 0; i<16; i++) {
			fingerTable.add(this.id);
		}
//		fingerTable.forEach((v) -> {v=this.id;});
	}

	@Override
	public void run() {
		new Thread() {
			public void run() {
			System.out.println("thread");
			System.out.println("add "+addr.getHostAddress());
			System.out.println("p "+port);
			Server s;
			try {
				s = new Server(new String[] { ""+port, "TLS_DHE_RSA_WITH_AES_128_CBC_SHA" });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			s.run();
		}}.start();
		// TODO Auto-generated method stub
//		while(true) {
			System.out.println("My ID id "+this.id);
			System.out.println("My successor is "+this.fingerTable.get(0));
		
//		}
	}

}
