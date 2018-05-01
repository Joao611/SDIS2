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
			try {
				new Client(new String[] {"127.0.1.1","9000", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA" }, peer.peerInfo.getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
		}
		peer.run();
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
		
		for (int i = 0; i<16; i++) {
			fingerTable.add(this.peerInfo);
		}
		previous = this.peerInfo;
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
			System.out.println("My successor is "+this.fingerTable.get(0).getId());
		
//		}
	}

	public String lookup(UnsignedByte key) {
		String res = new String();
//		I To resolve (lookup) a key k, node n forwards the request
//		to:
//		I The next node, i.e. FTn[1], if n < k < FTn[1]
//		I To node n
//		0 st n
//		0 = FTn[j] ≤ k < FTn[j + 1]
//		(All arithmetic in modulo 2m)
//		TODO mod 2m
		if(this.peerInfo.getId().equalTo(key)) { //I am the successor
			res = "Successor "+
					this.peerInfo.getId().getB()+
					" "+
					this.peerInfo.getPort()+
					" "+
					this.peerInfo.getAddr().getHostAddress()
					;
		}
		if((this.peerInfo.getId().smallerThan(key))) { //TODO: errado 
//				&& (key <= this.fingerTable.get(0).getId())) {
			res = "Successor "+
				this.fingerTable.get(0).getId()+
				" "+
				this.fingerTable.get(0).getPort()+
				" "+
				this.fingerTable.get(0).getAddr().getHostAddress()
				;
		}else {
//			if((this.peerInfo.getId() <= key)) {
//				res= 
//			}
			res = "TODO";
		}
		return res;
	}

}
