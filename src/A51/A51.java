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
	
	private static String inputFile = null;
	private static String key = null;
	private static String frameNumber = null;
	private static final int BUFFERSIZE = 64;
	
	
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
		byte fileChunk;
		byte[] encryptedBytes = new byte[BUFFERSIZE];
		byte keyStreamByte; 
		
		String outFile = dataHandler.getFileWithoutExtension() + 
						"[encrypted]." + dataHandler.getFileExtension();
		
		System.out.println("Encrypting: " + dataHandler.getFileName() + " --> " + outFile);
		
		keyStreamGenerator.init(); //initialization phase for the key stream
		
		for(long currByte = 0; currByte < fSize; currByte++) {
			//calculate the current frame
			try {
				fileChunk = dataHandler.readFileByte((int)currByte);
				keyStreamByte = keyStreamGenerator.getStreamByte();
				// XOR with Plain text and key stream --> cipher text 
				encryptedBytes[(int) (currByte % BUFFERSIZE)] = (byte) (keyStreamByte ^ fileChunk);
				
				if((currByte + 1) % BUFFERSIZE == 0) { //check if the buffer should be written
					dataHandler.writeFileBytes(encryptedBytes, outFile, BUFFERSIZE);
				}
				
				//if the loop will end in the next iteration, write the rest of the buffer
				else if(currByte == fSize - 1) { 
					dataHandler.writeFileBytes(encryptedBytes, outFile, (int)(currByte % BUFFERSIZE) + 1);
				}
				
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
		
		//Create new file handler and key stream handler for encryption
		DataFileHandler dataHandler = new DataFileHandler(fileToDecrypt);
		KeyStreamGenerator keyStreamGenerator = new KeyStreamGenerator(decryptKey, decryptFrameNumber);
		
		long fSize = dataHandler.getFileSize();
		byte fileChunk;
		byte[] decryptedBytes = new byte[BUFFERSIZE];
		byte keyStreamByte; 
		
		String outFile = dataHandler.getFileWithoutExtension() + "[decrypted]." + dataHandler.getFileExtension();
		System.out.println("Decrypting: " + dataHandler.getFileName() + " --> " + outFile);
		
		keyStreamGenerator.init(); //initialization phase for the key stream
		
		for(long currByte = 0; currByte < fSize; currByte ++) {
			//calculate the current frame
			try {
				fileChunk = dataHandler.readFileByte((int)currByte);
				keyStreamByte = keyStreamGenerator.getStreamByte();
				// XOR with cipher text and key stream --> plain text
				decryptedBytes[(int) (currByte % BUFFERSIZE)] = (byte) (keyStreamByte ^ fileChunk);
				
				if((currByte + 1) % BUFFERSIZE == 0) {
					dataHandler.writeFileBytes(decryptedBytes, outFile, BUFFERSIZE);
				}
				
				//if the loop will end in the next iteration, write the rest of the buffer
				else if(currByte - 1 == fSize) {
					dataHandler.writeFileBytes(decryptedBytes, outFile, (int)(currByte % BUFFERSIZE) + 1);
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return outFile;
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
			
			System.out.println("Key: " + key + 
					"\nFrame Number: " + frameNumber + "\n");
		}
		return;
	}
	
	/**
	 * Adds zeros to the MSB of the given string. If the string is
	 * too large, it will be truncated.
	 * @param inString
	 * @param bytes
	 * @return
	 */
	private static String padZeros(String inString, int bytes) {
		char[] initS = new char[bytes];
		char[] inArray = inString.toCharArray();
		
		if(inString.length() > bytes) {
			int start = inString.length() - bytes;
			System.arraycopy(inArray, start, initS, 0, initS.length);
		}
		else {
			int diff = bytes - inString.length();
			
			for(int i = 0; i < bytes; i++) initS[i] = '0';
			System.arraycopy(inArray, 0, initS, diff, inArray.length);
		}
		return new String(initS);
	}
	
	/**
	 * Entry point to the system.
	 * 3 Input arguments:
	 * args[0]: /path/to/file.ext
	 * args[1]: key
	 * args[2]: frame number
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		
		validateInput(args);
		
		String encryptedFile = encrypt(inputFile, key, frameNumber);
		String decryptedFile = decrypt(encryptedFile, key, frameNumber);
	}

}
