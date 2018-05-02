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

	public PeerInfo(String substring) {
		// TODO Auto-generated constructor stub
		String[] s = substring.split(" ");
		
		id = new UnsignedByte(Short.valueOf(s[3].substring(3, s[3].length()-2)));
		try {
			addr = InetAddress.getByName(s[4].substring(5, s[4].length()-1));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		port = Integer.valueOf(s[5].substring(5, s[5].length()-1));
		
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
		return id.getB();
	}


	/**
	 * @param id the id to set
	 */
	public void setId(UnsignedByte id) {
		this.id = id;
	}
	
	

}
