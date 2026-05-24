package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HexFormat;
import java.util.Scanner;

enum byteType{instruction, data}
/* general idea review:
 * at first nothing in caches
 * we should immediately miss
 * what happens when it misses?
 * it eventually calls fetchRAM
 * which calls put to cache -> it should indeed put to cache here
 * what if it hits at L1 (load for now)
 * good for it! just move on
 * what if it hits at L2?
 * move that data to L1 as well.
 * it does that!?
 * ok what if it is now store?
 * L1 hit? -> write to L2 and RAM as well(the new thing)
 * L2 hit? -> good, write to RAM too
 * no hit? -> even better (sorry dear clock cycles, write to RAM only)
 * modify is just 2 function calls i believe.
 * and that is pretty much it...
 * */
public class Main {
	static int L1s; static int L2s; // how many bits to represent set count
	static int L1b; static int L2b; // how many bits to represent block size (64 bytes would be 6 bits)
	static int L1E; static int L2E; // number of lines per set
	static int L1Ihits, L1Imisses, L1Ievictions;
	static int L1Dhits, L1Dmisses, L1Devictions;
	static int L2hits, L2misses, L2evictions;
	static Cache L1data;
	static Cache L1instruction;
	static Cache L2;
	static byte[] RAM;
	static int time;
	
	public static void main(String[] args) throws IOException {
		
		Scanner scanner = new Scanner(System.in);
		Path path = Paths.get("sys3\\RAM.dat");
		RAM = Files.readAllBytes(path);
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
		
		System.out.println("L1I-hits: " + L1Ihits + " L1I-misses: " + L1Imisses + " L1I-evictions: " + L1Ievictions + "\n"
				+ "L1D-hits: " + L1Dhits + " L1D-misses: " + L1Dmisses + " L1D-evictions: " + L1Devictions + " \n"
				+ "L2-hits: " + L2hits + " L2-misses: " + L2misses + " L2-evictions: " + L2evictions );
	}
	// op, address, size ^ op, address, size, data
	static void decipherInstruction(String instruction) {
		char op = instruction.charAt(0);
		String addressString = instruction.substring(2, 10);
		int address = Integer.parseUnsignedInt(addressString, 16);
		short lastCommaIndex = (short)instruction.lastIndexOf(','); 
		int size = instruction.charAt(12) - '0';
		
		if (lastCommaIndex <= 10) {	// op, address, size | no data, L or I
			if (op == 'L') {
				dataLoad(address, size);
			}
			else if(op == 'I') {
				instLoad(address, size);
			}
			else return; // error
		}
		else { // op, address, size, data | S or M 
			String dataString = instruction.substring(15);
			byte[] data = HexFormat.of().parseHex(dataString);
			if(op == 'S') {
				store(address, size, data);
			}
			else if(op == 'M') {
				modify(address, size, data);
			}
			else return; // error
		}		
	}
	// size is redundant for this project
	static void dataLoad(int address, int size){
		// search the L1d cache, found? HIT 
		if(L1data.search(address, size)) {
			L1Dhits++;
		}
		// not found? search L2 cache, found HIT
		else if(L2.search(address, size)) {
			L1Dmisses++; L2hits++;
			putToCache(L1data, L2.recentlyUsedLine.data, address);
		}
		else {
			// not found? search the RAM, fetch it
			L1Dmisses++; L2misses++;
			fetchRAM(address, size, src.byteType.data);
		}
	}
	// size is redundant for this project
	static void instLoad(int address, int size) {
		// search the L1i cache, found? HIT 
		if(L1instruction.search(address, size)) {
			L1Ihits++;
		}
		// not found? search L2 cache, found HIT
		else if(L2.search(address, size)) {
			L1Imisses++; L2hits++;
			putToCache(L1instruction, L2.recentlyUsedLine.data, address);
		}
		else {
			// not found? search the RAM, fetch it
			L1Imisses++; L2misses++;
			fetchRAM(address, size, src.byteType.instruction);
		}
	}
	
	static void store(int address, int size, byte[] data) {
		if(L1data.search(address, size)) {
			L1Dhits++;
			L1data.recentlyUsedLine.overwriteDataInsideBlock(L1data.offset, size, data);
			// also write to L2 
			
			L2.search(address, size); // so it sets the global variables
			L2.recentlyUsedLine.overwriteDataInsideBlock(L2.offset, size, data);
			L2hits++;
			// also write to ram
			storeRAM(address, size, data);
		}
		// not found? search L2 cache, found HIT
		else if(L2.search(address, size)) {
			L1Imisses++; L2hits++;
			L2.recentlyUsedLine.overwriteDataInsideBlock(L2.offset, size, data);
			storeRAM(address, size, data);
		}
		else {
			// not found? good honestly, just overwrite ram
			L1Imisses++; L2misses++;
			storeRAM(address, size, data);
		}
	}
	
	static void modify(int address, int size, byte[] block) {
		// data load followed by a store
		dataLoad(address, size);
		store(address, size, block);
	}
	
	static byte[] fetchRAM(int address, int size, byteType byteType) {
		int blockSize = 1 << L1b; // L2b is no different btw
		int startingAddressOffset = address % blockSize;
		int startingAddress = address - startingAddressOffset;
		byte[] fetchedData = new byte[blockSize];
		for(int i = 0; i < blockSize; i++) {
			fetchedData[i] = RAM[startingAddress + i];
		}
		putToCache(L2, fetchedData, address);
		if(byteType == src.byteType.data) {
			putToCache(L1data, fetchedData, address);
		} 
		else if (byteType == src.byteType.instruction) {
			putToCache(L1instruction, fetchedData, address);
		}
		return null;
	}
	
	static void storeRAM(int address, int size, byte[] dataToWrite) {
		// no-write allocate :D
		for(int i = 0; i < size; i++) {
			RAM[address + i] = dataToWrite[i];
		}
	}
	
	static void putToCache(Cache cache, byte[] block, int address) {
		cache.tagAndSetIdentifier(address);
		int tag = cache.searchedTag;
		int set = cache.searchedSet; 
		int lineNo = cache.availableLine(set);
		Line line = new Line(true, block, tag, time++);
		if (cache.cacheLines[set][lineNo] == null) {
			cache.cacheLines[set][lineNo] = line;
		}
		else if(cache.cacheLines[set][lineNo].valid) {
			// victim, eviction
			switch(cache.type) {
			case L1i:
				L1Ievictions++;
				break;
			case L1d:
				L1Devictions++;
				break;
			case L2:
				L2evictions++;
				break;
			default: break;
			}
		}
		cache.cacheLines[set][lineNo] = line;
		// is it normal only putToCache initializes lines??
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