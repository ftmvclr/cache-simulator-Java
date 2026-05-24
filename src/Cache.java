package src;
enum cacheType {L1i, L1d, L2};

public class Cache {
	int s; int b; int E; // bit counts 
	Line cacheLines[][];
	cacheType type;
	int searchedTag;
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
			if(line == null) continue;
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
	
	// return an invalid one OR the least recently used
	int availableLine(int set) { // TODO
		Line[] lines = cacheLines[set];
		int min = 0; int count = 0;
		for(Line line : lines) { 
			if(line == null) {
				return count;
			};
			if (!line.valid) {
				return count;
			}
			else if(line.time < lines[min].time){ // can't be equal
				min = count;
			}
			count++;
		}
		return min;
	}
}
