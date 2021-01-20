import java.util.*;

public class Add128 implements SymCipher {
    private byte[] key;

    // The parameterless constructor will create a random 128-byte additive key and store it inan array of bytes. 
    //SecureChatClien call
    public Add128(){
    	Random rand = new Random();
        key = new byte[128];
        rand.nextBytes(key);
    }
    
    // The other constructor will use the byte array parameter as its key. 
    //SecureChatServer call
    public Add128(byte[] byteArray) {
        key=byteArray;
    }

    
    @Override
    public byte[] getKey() {
        return key;
    }

   
    //if the message is shorter than the key, simply ignore the remaining bytes in the key. 
    //If the message is longer than the key, cycle through the key as many times as necessary. 
    //The encrypted array of bytes should be returned as a result of this method call.
    public byte[] encode(String S) {
    	if(S == null)
			throw new IllegalArgumentException();
    	//System.out.println("-----Add Encoding-----\n");
    	//convert the String parameter to an array of bytes 
        byte[] encrypted = S.getBytes();
        
        for(int i=0; i< encrypted.length; i++){
        	//add the corresponding byte of the key to each index in the array of bytes. 
        	encrypted[i] = (byte) (key[i % key.length] + encrypted[i]);
        }
        return encrypted;
        
    }

    // Decodes the given array of bytes by subtracting the corresponding number of bytes based on the index
    // Moves back to the beginning of the key when end is reached and continues decoding
    //If the message is shorter than the key, simply ignore the remaining bytes in the key. 
    //If the message is longer than the key, cycle through the key as many times as necessary. 
    
    public String decode(byte[] bytes) {
		if (bytes == null)
			throw new IllegalArgumentException();
		//System.out.print("-----Add Decoding-----\n ");

		byte[] decodes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			// subtract the corresponding byte of the key from each index of the array of
			// bytes.
			decodes[i] = (byte) (bytes[i] - key[i % key.length]);
		}

		// Convert the resulting byte array back to a String and return it.
		return new String(decodes);
    }
    
    
  //for testing purpose
  	public static void main(String[] args){
  		String s = "I just need a long string. A text string, also known as a string or simply as text, "
  				+ "is a group of characters that are used as data in a spreadsheet program. "
  				+ "Text strings are most often comprised of words, but may also include letters, numbers, "
  				+ "special characters, the dash symbol, or the number sign.";

  		System.out.println("parameterless constructor");
  		Add128 cipher = new Add128();
  		System.out.println("key: " + cipher.getKey().toString());
  		byte[] encode = cipher.encode(s);
  		System.out.println("encode: " + encode.toString());
  		String decode = cipher.decode(encode);
  		System.out.println("decode: " + decode);
  		
  		System.out.println();
  		System.out.println("byte array parameter");
  		byte[] key = cipher.getKey();
  		Add128 cipher2 = new Add128(key);
  		System.out.println("key: " + cipher2.getKey().toString());
  		byte[] encodeByte = cipher2.encode(s);
  		System.out.println("encode: " + encodeByte.toString());
  		String decodeByte = cipher2.decode(encodeByte);
  		System.out.println("decode: " + decodeByte);
  	}
}