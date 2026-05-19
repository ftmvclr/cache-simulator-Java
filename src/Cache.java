package src;

public class Cache {
	int s; int b; int E;
	String data;
	boolean valid; 
	enum cacheType {L1i, L1d, L2};
	cacheType type;
	
	Cache(int s, int b, int E, cacheType type){
		this.s = s; this.b = b;
		this.E = E;
		this.type = type;
	}
	
	void dataLoad(){
		
	}
	
	void store() {
		
	}
	
	void instLoad() {
		
	}
	
	void modify() {
		
	}
}
