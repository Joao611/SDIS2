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

	private static final int M = 8;
	private PeerInfo peerInfo;
	private ArrayList<PeerInfo> fingerTable = new ArrayList<PeerInfo>();
	private PeerInfo previous;
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
			Client c;
			try {
				c = new Client(new String[] {"127.0.1.1","9000", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA" }, peer.peerInfo.getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			peer.join(c);
		}
		peer.run();
	}

	private void join(Client c) {
		// TODO Auto-generated method stub
		PeerInfo p = c.run();
		
	}

	public PeerInChord(Integer port) {
		this.peerInfo = new PeerInfo(null,null, null);
		try {
			this.peerInfo.setAddr(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		this.peerInfo.setPort(port);
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		byte[] hash = digest.digest((this.peerInfo.getAddr().getHostAddress() + this.peerInfo.getPort()).getBytes(StandardCharsets.ISO_8859_1));
		this.peerInfo.setId(new UnsignedByte(ByteBuffer.wrap(hash).getShort()));
		
		for (int i = 0; i < M; i++) {
			fingerTable.add(null);
//			TODO: null desing patter
		}
		previous = null;
	}

	@Override
	public void run() {
		PeerInChord a= this;
		new Thread() {
			public void run() {
			System.out.println("thread");
			System.out.println("add "+peerInfo.getAddr().getHostAddress());
			System.out.println("p "+peerInfo.getPort());
			Server s;
			try {
				s = new Server(
						new String[] { ""+peerInfo.getPort(),
								"TLS_DHE_RSA_WITH_AES_128_CBC_SHA" },
						a);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			s.run();
		}}.start();
		// TODO Auto-generated method stub
//		while(true) {
			System.out.println("My ID id "+this.peerInfo.getId());
			System.out.println("My successor is "+ this.fingerTable.get(0).getId());
		
//		}
	}

	public String lookup(UnsignedByte key) {
		String res = null;
//		TODO mod 2m
		if(this.peerInfo.getId().equalTo(key)) { //I am the successor
			return "Successor "+ this.peerInfo.toString();
		}
		if((this.peerInfo.getId().smallerThan(key)) 
				&& (key.smallerThan(this.fingerTable.get(0).getId()))) {
			return "Successor "+ this.fingerTable.get(0).toString();
		} else {
			for(int i = M-1; i >= 0; i--) {
				if(this.peerInfo.getId().smallerThan(this.fingerTable.get(i).getId())
						&& this.fingerTable.get(i).getId().smallerThan(key)) {
					return "Ask "+ this.fingerTable.get(0).toString();
				}
			}
		}
		return "Error";
	}
	
	

//	private void fix_fingerTable() {
//		for(int i = 0; i < M; i++) {
//			this.fingerTable.set(i,
//					lookup(new UnsignedByte((short) (this.peerInfo.getId().getB()+Math.pow(2, i)))));
//		}
//		
//	}

}
