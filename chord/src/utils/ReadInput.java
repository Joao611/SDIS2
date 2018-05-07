package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import program.Peer;

public class ReadInput {

	public static void readInput(Peer peer) {
		while(true) {
			System.out.println("Choose an option: ");
			System.out.println("\t1. Backup");
			System.out.println("\t2. Restore");
			System.out.println("\t3. Delete");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			Integer op = null;
			try {
				op = Integer.valueOf(in.readLine());
			} catch (NumberFormatException e) {
				System.out.println("Error: not a valid input!");
							continue;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			switch (op) {
			case 1:{
				ReadInput.backupOption(in, peer);
				break;
			}
			case 2:{
				break;
			}
			case 3:{
				break;
			}
			default: {
				System.out.println("Error: not a valid input!");
							continue;
			}
			}}
	}

	private static void backupOption(BufferedReader in, Peer peer) {
		System.out.println("FileName:");
		String filename;
		try {
			filename = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		if(!Files.exists(Paths.get(filename))) {
			System.out.println("Error: file does not exist!");
			return;
		}

		System.out.println("Replication Degree:");
		Integer degree = null;
		try {
			degree = Integer.valueOf(in.readLine());
		} catch (NumberFormatException | IOException e) {
			System.out.println("Error: Invalid Input!");
			return;
		}
		if((degree < 1) || (degree > 9)) {
			System.out.println("Error: Invalid Input!");
			return;
		}
		try {
			peer.backup(filename, degree);
			System.out.println("Called Backup!");
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
			return;
		}
	}

}
