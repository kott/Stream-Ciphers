package A51;

import java.io.File;
import java.io.IOException;

/**
 * This class handles the encryption and decryption
 * of files. Given a file, encryption key (Hex) and frame
 * number (Hex), encryption and decryption functions can be
 * performed on the file using the A5/1 stream cipher algorithm. 
 *
 */
public class A51 {
	
	private static final int FRAMELENGTH = 57; //frames have 228 bits (57 bytes)
	private static String inputFile = null;
	private static String key = null;
	private static String frameNumber = null;
	
	/**
	 * 
	 * @param fileToEncrypt
	 * @param encryptKey
	 * @param encryptFrameNumber
	 * @return The filename of the encrypted file.
	 */
	public static String encrypt(String fileToEncrypt, String encryptKey, String encryptFrameNumber) {
		
		//Create new file handler and key stream handler for encryption
		DataFileHandler dataHandler = new DataFileHandler(fileToEncrypt);
		KeyStreamGenerator keyStreamGenerator = new KeyStreamGenerator(encryptKey, encryptFrameNumber);
		
		long fSize = dataHandler.getFileSize();
		byte[] fileChunk = new byte[FRAMELENGTH];
		byte[] encryptedFrame = new byte[FRAMELENGTH];
		byte[] keyStreamFrame; 
		int frame;
		int bytesToWrite = FRAMELENGTH;
		
		String outFile = dataHandler.getFileWithoutExtension() + 
						"(encrypted)." + dataHandler.getFileExtension();
		
		System.out.println("Encrypting: " + dataHandler.getFileName() + " --> " + outFile);

		for(long currByte = 0; currByte < fSize + FRAMELENGTH; currByte += FRAMELENGTH) {
			//calculate the current frame
			frame = keyStreamGenerator.getFrameNumber() - keyStreamGenerator.getOrigFrameNumber();
			keyStreamGenerator.init(); //initialization phase for the key stream
			
			try {
				fileChunk = dataHandler.readFileBytes(FRAMELENGTH, frame);
				keyStreamFrame = keyStreamGenerator.getStreamFrame();
				keyStreamGenerator.incrementFrameNumber(); //for each frame we need to increment the frame number
				
				for(int pos = 0; pos < FRAMELENGTH; pos++) {
					// XOR with Plain text and key stream --> cipher text 
					encryptedFrame[pos] = (byte) (keyStreamFrame[pos] ^ fileChunk[pos]);
					if(currByte + pos > fSize) { //a check to make sure we don't change the size of the file
						bytesToWrite = pos;
						break;
					}
				}
				dataHandler.writeFileBytes(encryptedFrame, outFile, bytesToWrite);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return outFile;
	}
	
	/**
	 * 
	 * @param fileToDecrypt
	 * @param decryptKey
	 * @param decryptFrameNumber
	 * @return The filename of the decrypted file.
	 */
	public static String decrypt(String fileToDecrypt, String decryptKey, String decryptFrameNumber) {
		
		DataFileHandler dataHandler = new DataFileHandler(fileToDecrypt);
		KeyStreamGenerator keyStreamGenerator = new KeyStreamGenerator(decryptKey, decryptFrameNumber);
		
		long fSize = dataHandler.getFileSize();
		byte[] fileChunk = new byte[FRAMELENGTH];
		byte[] decryptedFrame = new byte[FRAMELENGTH];
		byte[] keyStreamFrame; 
		int frame;
		int bytesToWrite = FRAMELENGTH;
		
		String outFile = dataHandler.getFileWithoutExtension() + "(decrypted)." + dataHandler.getFileExtension();
		System.out.println("Decrypting: " + dataHandler.getFileName() + " --> " + outFile);

		
		for(long currByte = 0; currByte < fSize + FRAMELENGTH; currByte += FRAMELENGTH) {
			//calculate the current frame
			frame = keyStreamGenerator.getFrameNumber() - keyStreamGenerator.getOrigFrameNumber();
			keyStreamGenerator.init(); // initialization phase for key stream
			
			try {
				fileChunk = dataHandler.readFileBytes(FRAMELENGTH, frame);
				keyStreamFrame = keyStreamGenerator.getStreamFrame();
				keyStreamGenerator.incrementFrameNumber();
				
				for(int pos = 0; pos < FRAMELENGTH; pos++) {
					// XOR with cipher text and key stream --> plain text 
					decryptedFrame[pos] = (byte) (keyStreamFrame[pos] ^ fileChunk[pos]);
					if(currByte + pos > fSize) {
						bytesToWrite = pos;
						break;
					}
				}
				dataHandler.writeFileBytes(decryptedFrame, outFile, bytesToWrite);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return outFile;
	}
	
	public static void printFrame(byte[] frame) {
		System.out.println();
		for(byte b : frame) {
			System.out.print("" + b);
		}
		System.out.println();
	}

	/**
	 * Validates the array of input. If an error occurs, the 
	 * program will exit with an error message.
	 * @param args
	 */
	public static void validateInput(String[] args) {
		boolean error = false;
		
		if(args.length < 3) {
			error = true;
			System.err.println("Not enough input arguments!\n"
					+ "Input Format: [Path-To-File] [Key (Hex)] [Frame Number (Hex)]");
		}
		if(!(new File(args[0])).exists()) {
			error = true;
			System.err.println("Given file does not exist!\n"
					+ "Given File: " + args[0]);
		}
		
		if(error) {
			System.exit(-1);
			return;
		}
		else {
			inputFile = args[0];
			key = padZeros(args[1], 16);
			frameNumber = padZeros(args[2], 8);
		}
		return;
	}
	
	/**
	 * Adds zeros to the MSB of the given string.
	 * @param inString
	 * @param bytes
	 * @return
	 */
	private static String padZeros(String inString, int bytes) {
		char[] initS = new char[bytes];
		char[] inArray = inString.toCharArray();

		for(int i = 0; i < bytes; i++) initS[i] = '0';
		
		return new String(initS);
	}
	
	public static void main(String[] args) throws IOException{
		
		validateInput(args);
		
		System.out.println(padZeros(args[1], 16));
		
		String encryptedFile = encrypt(inputFile, key, frameNumber);
		String decryptedFile = decrypt(encryptedFile, key, frameNumber);
	}

}
