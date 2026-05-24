package src;
enum cacheType {L1i, L1d, L2};

public class Cache {
	int s; int b; int E; // bit counts 
	Line cacheLines[][];
	cacheType type;
	private int searchedTag;
	int searchedSet;
	int offset;
	Line recentlyUsedLine;
	int recentlyUsedLineNo;
	
	Cache(int s, int b, int E, cacheType type){
		this.s = s; this.b = b;
		this.E = E;
		this.type = type;
		cacheLines = new Line[1 << s][E]; // S by E cache.
	}
	Cache(){}
	
	boolean search(int address, int size){
		tagAndSetIdentifier(address); // filled the fields tag and set (and offset)
		Line[] linesInCorrectSet = cacheLines[searchedSet];
		int count = 0;
		for(Line line : linesInCorrectSet) {
			if (line.tag == searchedTag) {
				recentlyUsedLine = line;
				recentlyUsedLineNo = count;
				return true;
			}
			count++;
		}
		return false;
	}
	
	void tagAndSetIdentifier(int address) {
		offset = address & ((1 << b) - 1);  // instead of Math.pow
		address = address >>> b;
		searchedSet = address & ((1 << s) - 1); // instead of Maht.pow 2, s - 1
		address = address >>> s;
		searchedTag = address;
	}
	
	int availableLine() { // TODO
		return -1;
	}
}
