package program;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import chord.ChordManager;
import communication.Server;
import database.Database;
import utils.ReadInput;
import utils.SingletonThreadPoolExecutor;

public class Peer {

	private ChordManager chordManager;
	private Server server;
	private Database database;
	private static Path path;
	private static int storageCapacity;
	private static int usedStorage = 0;


	public Peer(ChordManager chordManager, Server server, Database database, int storageCapacity) {
		this.chordManager = chordManager;
		this.server = server;
		this.database = database;
		this.storageCapacity = storageCapacity;
		this.server.setPeer(this);
	}

	public static void main(String[] args) {
		if(args.length < 1) {
			System.err.println("Error: Need a port Number");
			return;
		}
		Integer port = Integer.valueOf(args[0]);
		ChordManager chordManager = new ChordManager(port);

		generatePath(chordManager.getPeerInfo().getId());
		
		Server server;
		try {
			server = new Server(new String[] {"TLS_DHE_RSA_WITH_AES_128_CBC_SHA"}, port);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		chordManager.setDatabase(new Database());
		
		Peer peer = new Peer(chordManager,server, chordManager.getDatabase(), Integer.valueOf(args[1]));

		InetAddress addr = null;
		port = null;

		if(args.length >= 4) {
			try {
				addr = InetAddress.getByName(args[2]);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return;
			}
			port = Integer.valueOf(args[3]);
		}
		peer.joinNetwork(addr, port);
	}

	public void joinNetwork(InetAddress addr, Integer port) {

		if(addr != null) {
			chordManager.join(addr, port);
		}
		SingletonThreadPoolExecutor.getInstance().get().execute(server);
		SingletonThreadPoolExecutor.getInstance().get().execute(chordManager);

		ReadInput.readInput(this);
	}

	public ChordManager getChordManager() {
		return this.chordManager;
	}

//	/**
//	 * Generate a file ID
//	 * @param filename - the filename
//	 * @return Hexadecimal SHA-256 encoded fileID
//	 * @throws IOException, NoSuchAlgorithmException
//	 * */
//	public String getFileID(String filename) throws IOException, NoSuchAlgorithmException {
//		Path filePath = Paths.get(filename); //The filename, not FileID
//		BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
//		MessageDigest digest = MessageDigest.getInstance("SHA-256");
//		byte[] hash = digest.digest((filename + attr.lastModifiedTime()).getBytes(StandardCharsets.UTF_8));
//		return DatatypeConverter.printHexBinary(hash);
//	}
//	
	/**
	 * @return the p
	 */
	public static Path getPath() {
		return path;
	}

	/**
	 * @param p the p to set
	 */
	public static void setPath(Path p) {
		Peer.path = p;
	}
	
	/**
	 * Creates (if necessary) the directory where the chunks are stored
	 * @param id
	 */
	public static void generatePath(short id) {
		setPath(Paths.get("peer_" + id));
		if(!Files.exists(getPath())) {
			try {
				Files.createDirectory(getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns false if has space to store the chunk.
	 * 
	 * */
	public static boolean capacityExceeded(int amount) {
		if(usedStorage + amount > storageCapacity) {
			return true;
		}
		//atualizar espaco usado
		usedStorage += amount;
		return false;
	}

}
