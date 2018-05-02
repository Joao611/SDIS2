package program;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import chord.ChordManager;
import communication.Client;
import communication.Server;

public class Peer {
	
	private ThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
	private ChordManager chordManager;
	private Server server;
	
	
	public Peer(ChordManager chordManager, Server server) {
		this.chordManager = chordManager;
		this.server = server;
	}
	
	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("Need a port Number");
			return;
		}
		int port = Integer.valueOf(args[0]);
		ChordManager chordManager = new ChordManager(port);
		Server server;
		try {
			server = new Server(new String[] {"TLS_DHE_RSA_WITH_AES_128_CBC_SHA"},port,chordManager);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		Peer peer = new Peer(chordManager,server);
		
		InetAddress addr1 = null;
		Integer port1 = null;
		if(args.length >= 3) {
			try {
				addr1 = InetAddress.getByName(args[1]);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return;
			}
			port1 = Integer.valueOf(args[2]);
		}
		peer.go(addr1, port1);
	}

	public void go(InetAddress addr1, Integer port) {
		System.out.println("IN Peer.go()");
		if(addr1 != null) {
			chordManager.join(addr1, port);
		}
		threadPool.execute(server);
		threadPool.execute(chordManager);
		while(true) {
			
		}
	}
}
