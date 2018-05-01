/**
 * 
 */
package chord;

import java.net.InetAddress;

/**
 * @author anabela
 *
 */
public class PeerInfo {
	
	private UnsignedByte id;
	private InetAddress addr;
	private Integer port;

	public PeerInfo(UnsignedByte id, InetAddress addr, Integer port) {
		super();
		this.setId(id);
		this.setAddr(addr);
		this.setPort(port);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
//	@Override
//	public PeerInfo clone() {
//		return new PeerInfo(
//				id,
//				addr,
//				port);
//	}
	/**
	 * @return the addr
	 */
	public InetAddress getAddr() {
		return addr;
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
	public UnsignedByte getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(UnsignedByte id) {
		this.id = id;
	}
	
	

}
