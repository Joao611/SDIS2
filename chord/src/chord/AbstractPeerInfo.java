package chord;

import java.net.InetAddress;

import utils.UnsignedByte;

public abstract class AbstractPeerInfo {

	protected UnsignedByte id;
	protected InetAddress addr;
	protected Integer port;
	
	public abstract boolean isNull();
	
	public abstract InetAddress getAddr();
	public abstract Integer getPort();
	public abstract short getId();
}
