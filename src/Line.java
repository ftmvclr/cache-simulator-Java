package src;

public class Line extends Cache {
	boolean valid;
	byte[] data = new byte[1 << b]; // each element should hold 1 byte
	int time; // to find the least recently used one
	int tag;
	
	Line(boolean valid, byte[] data, int tag, int time){
		this.valid = valid;
		this.data = data;
		this.tag = tag;
		this.time = time;
	}
	void overwriteDataInsideBlock(int offset, int size, byte[] newData){
		for(int i = 0; i < newData.length; i++) {
			data[i] = newData[i];
		}
	}
}
