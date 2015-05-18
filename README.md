# A5-1
An implementation of the [A5/1 Stream Cipher](http://en.wikipedia.org/wiki/A5/1 "A5/1 Wiki Page").

## Run Configuration
The main runnable file is A51.java, which accepts 3 command line arguments:
  1. /path/to/file.ext
  2. 64-bit Key (in hexadecimal notation - cannot exceed 16 characters).
  For example: FEDCBA9876543210  is okay, but FFFEDCBA9876543210 is too large.
  3. 22-bit Frame Number (in hexadecimal notation).
  For example: C45AF  is okay, but C45AFF is too large.

## Expected Output
The program will produce two files. One file be the encrypted file written to
/path/to/file(encrypted).ext and the other will be the decrypted file which is 
written to /path/to/file(encrypted)(decrypted).ext.
