package A51;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *	This class is used for the operations associated with 
 *	the management of a given file. i.e. Read, Write, 
 *	Get file name, etc.
 *
 */
public class DataFileHandler {
	
	private String fileName;
	
	/**
	 * Initialise the class with the path to the image file
	 * 
	 * @param fileName
	 */
	public DataFileHandler(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Getter for the file name
	 * @return
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * Returns the extension of the current filename, if
	 * it can be found. If not, an empty string is returned.
	 * @return
	 */
	public String getFileExtension() {
		String ext = "";
		String[] fileChunks = this.fileName.split("[.]");
		
		if(fileChunks.length > 0) {
			ext = fileChunks[fileChunks.length - 1];
		}
		return ext;
	}
	
	/**
	 * Returns the path of the current filename, if
	 * it can be found. If not, an empty string is returned.
	 * @return
	 */
	public String getFilePath() {
		String path = "";
		int pos = this.fileName.lastIndexOf("/");
		
		if (pos > 0) {
		    path = this.fileName.substring(0, pos + 1);
		}
		return path;
	}
	
	/**
	 * This method reads in the image data from a given frame and returns
	 * an array of bytes(with the same length as our frame size).
	 * 
	 * @param frameLength
	 * @param frame
	 * @return
	 * @throws IOException
	 */
	public byte[] readFileBytes(int frameLength, int frame) throws IOException {
		FileInputStream fileInput = null; 
		byte[] buffer = new byte[frameLength];
		
		try  {
			//Open the input and out files for the stream 
			fileInput = new FileInputStream(this.fileName); 
			fileInput.read(buffer, frame * frameLength, frameLength);
			
		} 
		catch (IOException e) { 
			//Catch the IO error and print out the message 
			System.err.println(e.getMessage()); 
		}  
		finally  { 
			//close stream 
			if (fileInput != null) { 
				fileInput.close(); 
			}
		}
		return buffer;
	}
	
	/**
	 * 
	 * @param imageData
	 * @param fileName
	 * @throws IOException
	 */
	public void writeFileBytes(byte[] imageData, String fileName) throws IOException {
		File outFile = new File(fileName);
		FileOutputStream output = new FileOutputStream(outFile, true);
		output.write(imageData);
		output.close();
	}

}
