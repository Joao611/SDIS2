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
		SingletonThreadPoolExecutor.getInstance().get().execute(server);
		SingletonThreadPoolExecutor.getInstance().get().execute(chordManager);

		//cyclo while
		ReadInput.readInput(this);
	}

	public ChordManager getChordManager() {
		return this.chordManager;
	}

	public void backup(String fileName, int replicationDegree) throws NoSuchAlgorithmException, IOException {
		Path filePath = Paths.get(fileName);
		if(!Files.exists(filePath)) { 
			System.out.println("Error: File "+fileName+" does not exist: ");
			return;
		}
		Long numberOfChunks = null;
		String fileID = this.getFileID(fileName);

		numberOfChunks = (Math.floorDiv(Files.size(filePath), Utils.MAX_LENGTH_CHUNK))+1;
		
		int peerID = this.chordManager.getPeerInfo().getId();

		LocalState.getInstance().getBackupFiles().put(fileID, new BackupFile(fileName, peerID, replicationDegree));
		int chunkNo = 0;
		
		while(chunkNo < numberOfChunks) {

			AsynchronousFileChannel channel;
			try {
				channel = AsynchronousFileChannel.open(filePath);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			ByteBuffer body = ByteBuffer.allocate(Utils.MAX_LENGTH_CHUNK);
			int numberOfChunk = chunkNo;
			CompletionHandler<Integer, ByteBuffer> reader =new CompletionHandler<Integer, ByteBuffer>() {
				@Override
				public void completed(Integer result, ByteBuffer buffer) {
					buffer.flip();
					byte[] data = new byte[buffer.limit()];
					buffer.get(data);
					buffer.clear();
					try {
						backupChunk(numberOfChunk, replicationDegree, data, fileID, fileName);
					} catch (UnsupportedEncodingException | InterruptedException e) {
						e.printStackTrace();
					} 

				}

				@Override
				public void failed(Throwable arg0, ByteBuffer arg1) {
					System.err.println("Error: Could not read!");

				}

			};
			//channel.read(body, Utils.MAX_LENGTH_CHUNK*chunkNo, body, reader);
			chunkNo++;
		}
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
	 * @param chunkNo
	 * @param replicationDegree
	 * @param bodyOfTheChunk
	 * @param fileID
	 * @param filename
	 * @param isEnhancement
	 * @throws InterruptedException
	 * @throws UnsupportedEncodingException
	 */
	public void backupChunk(int chunkNo, int replicationDegree, byte[] bodyOfTheChunk, String fileID, String fileName) throws InterruptedException, UnsupportedEncodingException {

		int peerID = this.chordManager.getPeerInfo().getId();

		//guardar chunk com replication degree a -1. assim quando o backup for feito e ele receber uma resposta com o replication degree atual do chunk, atualiza da hashmap
		//		LocalState.getInstance().saveChunk(fileID, fileName, peerID, replicationDegree, chunk);
		//		LocalState.getInstance().decreaseReplicationDegree(fileID, chunk.getID(), peerID, peerID);
		

		//enviar a mensagem de PUTCHUNK
		SendPutChunk subprotocol = new SendPutChunk(peerID, fileID, fileName, chunkNo, replicationDegree, bodyOfTheChunk);
		SingletonThreadPoolExecutor.getInstance().get().submit(subprotocol);
		return;
	}

}
