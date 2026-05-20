package src;
enum cacheType {L1i, L1d, L2};

public class Cache {
	int s; int b; int E;
	Line cacheLines[][];
	cacheType type;
	
	Cache(int s, int b, int E, cacheType type){
		this.s = s; this.b = b;
		this.E = E;
		this.type = type;
		cacheLines = new Line[(int) Math.pow(2, s)][E]; // S by E cache.
	}
	Cache(){}
	
	boolean search(){
		return false;
	}
	
	void dataLoad(){
		
	}
	
	void store() {
		
	}
	
	void instLoad() {
		
	}
	
	void modify() {
		
	}
	private void tagAndSetIdentifier(int address) {
		
	}
}
