package src;

public class Line extends Cache {
	boolean valid;
	byte[] data = new byte[(int)Math.pow(2, b)]; // each element should hold 1 byte. = new byte[2^b of said cache]
	int tag;
	
	Line(boolean valid, byte[] data, int tag ){
		this.valid = valid;
		this.data = data;
		this.tag = tag;
	}
}
