package A51;

import java.io.IOException;

public class A51 {
	
	private static final int FRAMELENGTH = 57; //frames have 228 bits (57 bytes)
	private static String inputFile = null;
	private static String key = null;
	private static String frameNumber = null;
	private static DataFileHandler dataHandler;
	private static KeyStreamGenerator keyStreamGenerator;
	
	public static void encrypt() {
		
	}
	
	public static void decrypt() {
		
	}

	public static void main(String[] args) throws IOException{
		
		if(args.length < 3) {
			System.err.println("Not enough input arguments!\n"
					+ "Input Format: [Path-To-File] [Key (Hex)] [Frame Number (Hex)]");
			System.exit(-1);
		}
		else {
			inputFile = args[0];
			key = args[1];
			frameNumber = args[2];
		}
	
		//initialise a new DataHandler and KeyStreamGenerator
		dataHandler = new DataFileHandler(inputFile);
		keyStreamGenerator = new KeyStreamGenerator(key, frameNumber);
		
		encrypt();
		
		decrypt();
		

//		byte[] imBytes = dataHandler.readFileBytes(FRAMELENGTH, 0);
//		for(byte b : imBytes) {
//			//System.out.println((0XFF & b));
//		}
	}

}
