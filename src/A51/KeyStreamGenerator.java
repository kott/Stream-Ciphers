package A51;

import java.math.BigInteger;

/**
 * This Class generates the key stream for A5/1. The Stream is generated
 * based on the given key and frame number given to LFSR  
 *
 */
public class KeyStreamGenerator {
	private static final int KEYLENGTH = 64;
	private static final int FRAMENUMBERLENGTH = 22;
	private static final int FRAMELENGTH = 57; //frames have 228 bits (57 bytes)
	
	private long key; //64-bit key
	private int frameNumber; //22-bit frame number
	private int origFrameNumber; //keep the original frame number
	
	//Shift registers originally initialised with 0's 
	//They should be though of as LSB --> MSB. This being array[0]
	//is the LSB of the register.
	private byte[] r1 = new byte[19]; 
	private byte[] r2 = new byte[22];
	private byte[] r3 = new byte[23];
	
	private static final byte[] R1TAPS = {18, 17, 16, 13}; //LSB --> MSB
	private static final byte[] R2TAPS = {21, 20}; 		   //LSB --> MSB 
	private static final byte[] R3TAPS = {22, 21, 20, 7};  //LSB --> MSB 
	
	//clocking bits
	private static final byte R1CLK = 8;
	private static final byte R2CLK = 10;
	private static final byte R3CLK = 10;
	
	public KeyStreamGenerator(String key, String frameNumber) {
		//convert to primitive data types so that we can easily work with them
		this.key = new BigInteger(key, 16).longValue(); //this.key = Long.parseLong(key, 16);
		this.frameNumber = Integer.parseInt(frameNumber, 16);
		this.origFrameNumber = this.frameNumber;
		
		// need to check if the given Key and Frame Number are not too large
		if(this.key > Math.pow(2, 64) - 1 || this.frameNumber > Math.pow(2, 22) - 1) {
			throw new NumberFormatException("Either Key or Frame Number is too large");
		}
	}
	
	/**
	 * The key stream initialisation phase:
	 * [1] Mix in the Key (64 Cycles, regular clocking)
	 * [2] Mix in Frame Number (22 Cycles, regular clocking)
	 * [3] Run Registers for 100 cycles with irregular clocking
	 */
	public void init() {
		mixKey();
		mixFrameNumber();
		
		//initialise the LFSR for 100 cycles
		for(int i = 0; i < 100; i++) {
			regularCycle();
		}
	}
	
	/**
	 * Allow the frame number to be incremented
	 */
	public void incrementFrameNumber() {
		this.frameNumber++;
		return;
	}
	
	public int getFrameNumber() {
		return this.frameNumber;
	} 
	
	public int getOrigFrameNumber() {
		return this.origFrameNumber;
	}
	
	public void resetFrameNumber() {
		this.frameNumber = this.origFrameNumber;
	}
	
	/**
	 * This method generates a single key stream Frame. This should only
	 * be called once the initialisation phase has finished.
	 *   
	 * @return The stream frame contains the 57 bytes given by the LFSRs 
	 */
	public byte[] getStreamFrame() {
		byte[] streamFrame = new byte[FRAMELENGTH];
		StringBuilder sb;
		
		for(int i = 0; i < FRAMELENGTH; i++) {
			sb = new StringBuilder();
			for(int j = 0; j < 8; j++) { //get 8 bits from the LFSR in order to create a byte value
				byte regOut = (byte) ((r1[18] + r2[21] + r3[22]) % 2); //get the bit value of the LFSR
				sb.append(regOut); //convert the bit to string representation
				regularCycle(); // Send a clock pulse
			}
			streamFrame[i] = (byte) Integer.parseInt(sb.toString(), 2);
		}
		return streamFrame;
	}
	
	/**
	 * Mixes in the Key for initialisation of 
	 * the 3 LFSR.
	 */
	private void mixKey() {
		int count = 0;
		byte result, lsbKey;
		
		while (count < KEYLENGTH) {
			lsbKey = (byte)(key >>> count & 0x1); //get the LSB from the key
			result = (byte) ((lsbKey + r1[0]) % 2); // modulo 2 arithmetic with bit in LSB of R1
			shiftRegister(1, result);
			
			result = (byte) ((lsbKey + r2[0]) % 2); // modulo 2 arithmetic with bit in LSB of R2
			shiftRegister(2, result);
			
			result = (byte) ((lsbKey + r3[0]) % 2); // modulo 2 arithmetic with bit in LSB of R3
			shiftRegister(3, result);
			count++;
		}
	}
	
	/**
	 * Mixes in the frame number for initialisation 
	 * of the 3 LFSR.
	 */
	private void mixFrameNumber() {
		int count = 0;
		byte result, lsbKey;
		
		while (count < FRAMENUMBERLENGTH) {
			lsbKey = (byte)(frameNumber >>> count & 0x1); //get the LSB from the frame number
			result = (byte) ((lsbKey + r1[0]) % 2); // modulo 2 arithmetic with bit in LSB of R1
			shiftRegister(1, result);
			
			result = (byte) ((lsbKey + r2[0]) % 2); // modulo 2 arithmetic with bit in LSB of R2
			shiftRegister(2, result);
			
			result = (byte) ((lsbKey + r3[0]) % 2); // modulo 2 arithmetic with bit in LSB of R3
			shiftRegister(3, result);
			count++;
		}
	}
	
	/**
	 * Puts the LFSR through a regular cycle
	 * using the majority rule with the clocking bits.
	 */
	private void regularCycle() {
		byte i = 0;
		byte val1, val2, val3;
		
		byte vote = majorityVote();
		
		/****R1*****/
		if(r1[R1CLK] == vote) {
			val1 = clockRegister(1);
			shiftRegister(1, val1);
		}
		
		/****R2*****/
		if(r2[R2CLK] == vote) {
			val2 = clockRegister(2);
			shiftRegister(2, val2);
		}
		
		/****R3*****/
		if(r3[R3CLK] == vote) {
			val3 = clockRegister(3);
			shiftRegister(1, val3);
		}
	}
	
	/**
	 * Returns either 0 or 1. The return value indicates
	 * which CLK bits have the majority. i.e. If 1 is returned,
	 * that means that the clock bits whose value is 1 have
	 * the majority and will therefore be clocked.
	 * @return 
	 */
	private byte majorityVote() {
		byte res = (byte) (r1[R1CLK] + r2[R2CLK] + r3[R3CLK]);
		if(res > 2)
			return 1;
		return 0;
	}
	
	/**
	 * 
	 * @param register
	 * @param inVal
	 */
	public byte clockRegister(int register) {
		byte output = 0;
		
		switch(register) {
			case 1: //register 1
				
				for(byte tap : R1TAPS) {
					output += r1[tap]; // add all tapped bits
				}
				output = (byte) (output % 2);
				
				break;
			
			case 2: //register 2
				
				for(byte tap : R2TAPS) {
					output += r2[tap]; // add all tapped bits
				}
				output = (byte) (output % 2);
				
				break;
				
			case 3: //register 3
				
				for(byte tap : R3TAPS) {
					output += r3[tap]; // add all tapped bits
				}
				output = (byte) (output % 2);
				
				break;
				
			default:
				throw new IllegalArgumentException("Invalid Register Value");
		}
		return output;
	}
	
	/**
	 * Shifts the contents of the registers. This should mimic the 
	 * left shift operator (<<) but instead of just having the LSB
	 * become 0's, this method allows us to add in a desired value to
	 * the LSB.
	 * 
	 * @param register :the associated register
	 * @param input :the value that will be inserted to the LSB
	 * @return : the value of the register that was the MSB
	 */
	private byte shiftRegister(int register, byte input) {
		byte output = 0; //default value
		
		switch(register) {
		case 1: //register 1
			output = r1[r1.length -1];
			for(int i = r1.length - 1; i > 0; i--) {
				r1[i] = r1[i-1];
			}
			r1[0] = input;
			break;
		
		case 2: //register 2
			output = r2[r2.length -1];
			for(int i = r2.length - 1; i > 0; i--) {
				r2[i] = r2[i-1];
			}
			r2[0] = input;
			break;
			
		case 3: //register 3
			output = r3[r3.length -1];
			for(int i = r3.length - 1; i > 0; i--) {
				r3[i] = r3[i-1];
			}
			r3[0] = input;
			break;
			
		default:
			throw new IllegalArgumentException("Invalid Register Value");
		}
		return output;
	}
	
	/** 
	 * Print out the contents of the registers
	 * ***Used for debugging purposes***
	 */
	private void printRegisters() {
		
		System.out.println("******R1******");
		for(byte b : r1) {
			System.out.print("|" + b + "|");
		}
		System.out.println();
		System.out.println("******R2******");
		for(byte b : r2) {
			System.out.print("|" + b + "|");
		}
		System.out.println();
		System.out.println("******R3******");
		for(byte b : r3) {
			System.out.print("|" + b + "|");
		}
		System.out.println();
	}
}
