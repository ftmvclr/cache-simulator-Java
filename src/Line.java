package src;

public class Line extends Cache {
	boolean valid;
	byte[] data = new byte[1 << b]; // each element should hold 1 byte
	
	Line(boolean valid, byte[] data, int tag ){
		this.valid = valid;
		this.data = data;
		this.tag = tag;
	}
	
}
