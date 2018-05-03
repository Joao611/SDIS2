package chord;

import java.net.InetAddress;

import utils.UnsignedByte;

public abstract class AbstractPeerInfo {

	private UnsignedByte id;
	private InetAddress addr;
	private Integer port;
	
	public abstract boolean isNull();
	
	public abstract InetAddress getAddr();
	public abstract Integer getPort();
	public abstract short getId();
}
