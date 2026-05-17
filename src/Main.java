package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
	public static void main(String[] args) throws IOException {
		Path path = Paths.get("sys3\\RAM.dat");
		byte[] RAM = Files.readAllBytes(path);
	}
}
