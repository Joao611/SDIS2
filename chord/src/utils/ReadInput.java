package utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import database.BackupRequest;
import database.DBUtils;
import program.Peer;

public class ReadInput {

	public static void readInput(Peer peer) {
		while(true) {
			System.out.println("Choose an option: ");
			System.out.println("\t1. Backup");
			System.out.println("\t2. Restore");
			System.out.println("\t3. Delete");
			Scanner scanner = new Scanner(System.in);
			Integer op = null;
			op = scanner.nextInt();
			switch (op) {
			case 1:{
				ReadInput.backupOption(scanner, peer);
				break;
			}
			case 2:{
				break;
			}
			case 3:{
				ReadInput.deleteOption(scanner, peer);
				break;
			}
			default: {
				System.out.println("Error: not a valid input!");
				continue;
			}
			}}
	}
	private static void deleteOption(Scanner scanner, Peer peer) {
		ArrayList<BackupRequest> allRequests = DBUtils.getBackupsRequested(peer.getConnection());
		if (allRequests.size() > 0) {
			int option = -1;
			do {
				System.out.println("Select a file to delete:");
				for (int i = 0; i < allRequests.size(); i++) {
					System.out.println(i + ". " + allRequests.get(i).getFilename() + " -> " + allRequests.get(i).getFileId());
				}
				try {
					option = scanner.nextInt();
				}catch(InputMismatchException e) {
					System.out.println("Invalid Input");
					scanner.nextLine();
				}
			} while(option < 0 || option >= allRequests.size());
			peer.delete(allRequests.get(option).getFileId());
			
		} else {
			System.out.println("You need to backup files before deleting");
		}
		

	}

	private static void backupOption(Scanner in, Peer peer) {
		System.out.println("FileName:");
		String filename;
		filename = in.next();
		if(!Files.exists(Paths.get(filename))) {
			System.out.println("Error: file does not exist!");
			return;
		}
		Integer degree = 0;
		do {
			System.out.println("Replication Degree (1-9):");
			try {
				degree = in.nextInt();
			}catch(InputMismatchException e) {
				System.out.println("Invalid Input");
				in.nextLine();
			}
		} while((degree < 1) || (degree > 9));
			
		peer.backup(filename, degree,null);
		System.out.println("Called Backup!");
	}

}
