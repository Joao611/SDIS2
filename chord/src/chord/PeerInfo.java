/**
 * 
 */
package chord;

import java.net.InetAddress;
import java.net.UnknownHostException;

import utils.UnsignedByte;
import utils.Utils;

/**
 * @author anabela
 *
 */
public class PeerInfo extends AbstractPeerInfo {
	
//	private UnsignedByte id;
//	private InetAddress addr;
//	private Integer port;

	public PeerInfo(UnsignedByte id, InetAddress addr, Integer port) {
		this.id = id;
		this.addr = addr;
		this.port = port;
	}

	public PeerInfo(String str) {
		Utils.log(str);
		String[] attr = str.split("\r\n");

		Utils.log("----");
		Utils.log(attr[1]);
		attr = attr[1].split(" ");
		this.id = new UnsignedByte(Short.valueOf(attr[0]));

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
		return new String[]{id.toString(),addr.getHostAddress(),port.toString()}; 
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
