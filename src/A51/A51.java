package A51;

import java.io.IOException;

public class A51 {
	
	private static final int FRAMELENGTH = 57; //frames have 228 bits (57 bytes)

	public static void main(String[] args) throws IOException{
		
		System.out.println("A5/1  Stream Cipher");
		String imgFile = "/Users/kristianott/Documents/workspace/MK105 - Project/res/DSC01338.JPG";
		DataFileHandler dataHandler = new DataFileHandler(imgFile);
		System.out.println(dataHandler.getFilePath());
		byte[] imBytes = dataHandler.readFileBytes(FRAMELENGTH, 0);
		
		for(byte b : imBytes) {
			System.out.println((0XFF & b));
		}
		
		
		
	}

}
