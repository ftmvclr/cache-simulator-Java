package src;
enum cacheType {L1i, L1d, L2};

public class Cache {
	int s; int b; int E; // bit counts 
	Line cacheLines[][];
	cacheType type;
	int tag;
	int set;
	int offset;
	
	Cache(int s, int b, int E, cacheType type){
		this.s = s; this.b = b;
		this.E = E;
		this.type = type;
		cacheLines = new Line[(int) Math.pow(2, s)][E]; // S by E cache.
	}
	Cache(){}
	
	boolean search(int address, int size){
		tagAndSetIdentifier(address); // filled the fields tag and set (and offset)
		Line[] linesInCorrectSet = cacheLines[set];
		for(Line line : linesInCorrectSet) {
			if (line.tag == tag) {
				return true;
			}
		}
		return false;
	}
	
	void tagAndSetIdentifier(int address) {
		offset = address & ((1 << b) - 1);  // instead of Math.pow
		address = address >>> b;
		set = address & ((1 << s) - 1); // instead of Maht.pow 2, s - 1
		address = address >>> s;
		tag = address;
	}
}
