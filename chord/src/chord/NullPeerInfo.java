package chord;

import java.net.InetAddress;

public class NullPeerInfo extends AbstractPeerInfo{

	@Override
	public boolean isNull() {
		return true;
	}

	@Override
	public InetAddress getAddr() {
		return null;
	}

	@Override
	public Integer getPort() {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String[] asArray() {
		return new String[]{"null"};
	}

}
