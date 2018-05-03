package program;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import chord.ChordManager;
import chord.Stabilize;
import communication.Server;

public class Peer {
	
	private ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
	private ChordManager chordManager;
	private Server server;
	
	
	public Peer(ChordManager chordManager, Server server) {
		this.chordManager = chordManager;
		this.server = server;
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("Error: Need a port Number");
			return;
		}
		Integer port = Integer.valueOf(args[0]);
		ChordManager chordManager = new ChordManager(port);

		Server server;
		try {
			server = new Server(new String[] {"TLS_DHE_RSA_WITH_AES_128_CBC_SHA"}, port, chordManager);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		Peer peer = new Peer(chordManager,server);
		
		InetAddress addr = null;
		port = null;

		if(args.length >= 3) {
			try {
				addr = InetAddress.getByName(args[1]);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return;
			}
			port = Integer.valueOf(args[2]);
		}
		peer.joinNetwork(addr, port);
	}

	public void joinNetwork(InetAddress addr, Integer port) {
		
		if(addr != null) {
			chordManager.join(addr, port);
		}
		this.threadPool.execute(server);
		this.threadPool.execute(chordManager);
		this.threadPool.execute(new Stabilize(this));
		
		while(true) {
			//TODO: recebe pedidos da appTest
		}
	}

	public ScheduledThreadPoolExecutor getThreadPool() {
		return this.threadPool;
	}

	public ChordManager getChordManager() {
		return this.chordManager;
	}

}
