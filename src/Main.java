package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import src.Cache.cacheType;

public class Main {
	static int L1s; static int L2s; // how many bits to represent set count
	static int L1b; static int L2b; // how many bits to represent block size (64 bytes would be 6 bits)
	static int L1E; static int L2E; // number of lines per set
	static Cache L1data;
	static Cache L1instruction;
	static Cache L2;
	public static void main(String[] args) throws IOException {
		
		Scanner scanner = new Scanner(System.in);
		Path path = Paths.get("sys3\\RAM.dat");
		byte[] RAM = Files.readAllBytes(path);
		System.out.println("Put them in this order:\n"
				+ "L1s L1E L1b L2s L2E L2b");
		L1s = scanner.nextInt(); L1E = scanner.nextInt();
		L1b = scanner.nextInt(); L2s = scanner.nextInt();
		L2E = scanner.nextInt(); L2b = scanner.nextInt();
		scanner.close();
		initializeCaches();
		
		String instructions = Files.readString(Path.of("sys3\\test_small.trace"));	
		Scanner insScanner = new Scanner(instructions);
		while(insScanner.hasNextLine()) {
			String singleInstruction = insScanner.nextLine();
			decipherInstruction(singleInstruction);
		}
		insScanner.close();
	}
	// op, address, size ^ op, address, size, data
	static void decipherInstruction(String instruction) {
		char op = instruction.charAt(0);
		int address = Integer.parseInt(instruction.substring(2, 11));
		short lastCommaIndex = (short)instruction.lastIndexOf(','); 
		int size = instruction.charAt(12) - '0';
		
		if (lastCommaIndex <= 10) {	// op, address, size | no data, L or I
			if (op == 'L') {
				
			}
			else if(op == 'I') {
				
			}
			else return; // error
		}
		else { // op, address, size, data | S or M 
			String data = instruction.substring(15);
			if(op == 'S') {
				
			}
			else if(op == 'M') {
				
			}
			else return; // error
		}		
	}
	static void initializeCaches(){
		L1data = new Cache(L1s, L1b, L1E, cacheType.L1d);
		L1instruction = new Cache(L1s, L1b, L1E, cacheType.L1i);
		L2 = new Cache(L2s, L2b, L2E, cacheType.L2);
	}
}

//L1I-hits:0 L1I-misses:1 L1I-evictions:0
//L1D-hits:1 L1D-misses:1 L1D-evictions:0
//L2-hits:1 L2-misses:2 L2-evictions:0