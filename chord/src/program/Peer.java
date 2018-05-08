package program;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

import chord.ChordManager;
import communication.Server;
import database.Database;
import initiator.Peer;
import state_info.Chunk;
import state_info.BackupFile;
import state_info.LocalState;
import subprotocols.SendPutChunk;
import utils.ReadInput;
import utils.SingletonThreadPoolExecutor;
import utils.Utils;

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
			server = new Server(new String[] {"TLS_DHE_RSA_WITH_AES_128_CBC_SHA"}, port, chordManager);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		Database database = new Database();

		Peer peer = new Peer(chordManager,server, database, Integer.valueOf(args[1]));

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
		chordManager.setDatabase(database);
		peer.joinNetwork(addr, port);
	}

	public void joinNetwork(InetAddress addr, Integer port) {

		if(addr != null) {
			chordManager.join(addr, port);
		}
		SingletonThreadPoolExecutor.getInstance().get().execute(server);
		SingletonThreadPoolExecutor.getInstance().get().execute(chordManager);

		//cyclo while
		ReadInput.readInput(this);
	}

	public ChordManager getChordManager() {
		return this.chordManager;
	}

	/**
	 * Generate a file ID
	 * @param filename - the filename
	 * @return Hexadecimal SHA-256 encoded fileID
	 * @throws IOException, NoSuchAlgorithmException
	 * */
	public String getFileID(String filename) throws IOException, NoSuchAlgorithmException {
		Path filePath = Paths.get(filename); //The filename, not FileID
		BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest((filename + attr.lastModifiedTime()).getBytes(StandardCharsets.UTF_8));
		return DatatypeConverter.printHexBinary(hash);
	}
	
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
	
	/*
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
