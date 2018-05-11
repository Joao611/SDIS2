/**
 * 
 */
package chord;

import java.net.InetAddress;
import java.net.UnknownHostException;

import utils.Utils;

/**
 * @author anabela
 *
 */
public class PeerInfo extends AbstractPeerInfo {
	
//	private UnsignedByte id;
//	private InetAddress addr;
//	private Integer port;

	public PeerInfo(String id, InetAddress addr, Integer port) {
		this.id = id;
		this.addr = addr;
		this.port = port;
	}

	public PeerInfo(String str) {
		String[] attr = str.split("\r\n");

		attr = attr[1].split(" ");
		this.id = attr[0];

		try {
			this.addr = InetAddress.getByName(attr[1]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		this.port = Integer.valueOf(attr[2]);
		
	}

	/**
	 * @return the addr
	 */
	public InetAddress getAddr() {
		return addr;
	}

	@Override
	public String[] asArray() {
		return new String[]{id,addr.getHostAddress(),port.toString()}; 
	}

	/**
	 * @param addr the addr to set
	 */
	public void setAddr(InetAddress addr) {
		this.addr = addr;
	}


	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}


	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean isNull() {
		return false;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof PeerInfo))return false;
	    PeerInfo otherPeer = (PeerInfo)other;
	    if (otherPeer.getId().equals(this.id)) {
	    	return true;
	    }
	    return false;
	}
	

}
