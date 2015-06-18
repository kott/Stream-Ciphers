# A5-1
An implementation of the [A5/1 Stream Cipher](http://en.wikipedia.org/wiki/A5/1 "A5/1 Wiki Page").

## Java Version
This Program was tested using Java version 1.7.

## Run Configuration
The main file is A51.java, which accepts 3 command line arguments:
  1. /path/to/file.ext
  2. 64-bit Key (in hexadecimal notation - cannot exceed 16 characters).
  For example: FEDCBA9876543210  is okay, but FFFEDCBA9876543210 is too large.
  3. 22-bit Frame Number (in hexadecimal notation).
  For example: C45AF  is okay, but C45AFF is too large.

### Compiling and Running
1. Navigate to the same directory as this file. There should be a bin/ directory and a src/ directory
2. Compile with the following command: javac -d ./bin ./src/*.java
3. Run the program with the following command (and appropriate input arguments): 
java -classpath ./bin A51.A51 

### Running the Executable
1. Navigate to the runnable/ directory.
2. Enter the following command to run the .jar file (with proper input arguments):
java -jar A51.jar
3. Example: java -jar A51.jar test_image.jpg FEDCBA9876543210 C45AF

## Expected Output
The program will produce two files. One file be the encrypted file written to
/path/to/file[encrypted].ext and the other will be the decrypted file which is 
written to /path/to/file[encrypted][decrypted].ext.
