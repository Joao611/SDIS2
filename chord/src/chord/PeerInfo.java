/**
 * 
 */
package chord;

import java.net.InetAddress;
import java.net.UnknownHostException;

import utils.UnsignedByte;

/**
 * @author anabela
 *
 */
public class PeerInfo extends AbstractPeerInfo {
	
//	private UnsignedByte id;
//	private InetAddress addr;
//	private Integer port;

	public PeerInfo(UnsignedByte id, InetAddress addr, Integer port) {
		super();
		this.id = id;
		this.addr = addr;
		this.port = port;
	}

	public PeerInfo(String str) {
		String[] attr = str.split(" ");
		
		this.id = new UnsignedByte(Short.valueOf(attr[3].substring(3, attr[3].length() - 2)));

		try {
			this.addr = InetAddress.getByName(attr[4].substring(5, attr[4].length() - 1));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}
		this.port = Integer.valueOf(attr[5].substring(5, attr[5].length()-1));
		
	}

	/**
	 * @return the addr
	 */
	public InetAddress getAddr() {
		return addr;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PeerInfo [id=" + id + ", addr=" + addr.getHostAddress() + ", port=" + port + "]";
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
	public short getId() {
		return id.get();
	}


	/**
	 * @param id the id to set
	 */
	public void setId(UnsignedByte id) {
		this.id = id;
	}

	@Override
	public boolean isNull() {
		return false;
	}
	
	

}
