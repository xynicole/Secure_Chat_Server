import java.util.*;

public class Substitute implements SymCipher {
	private byte[] key; // key to encode words
	private byte[] inverseKey; // inverse key to decode words

	// parameterless constructor will create a random 256-byte array
	// which is a permutation of the 256 possible byte values and will serve as a
	// map from bytes to their substitution values
	public Substitute() {

		// create random 256 byte array
		ArrayList<Byte> ranByte = new ArrayList<Byte>();
		key = new byte[256];
		inverseKey = new byte[256];

		for (int i = 0; i < 256; i++) {
			ranByte.add((byte) i);
		}

		// shuffle to randomize the key
		Collections.shuffle(ranByte);

		// create the inverse key array and the decoded values
		for (int i = 0; i < 256; i++) {
			key[i] = ranByte.get(i);

			// convert negative index signed bytes to unsigned integers
			Byte index = new Byte(key[i]);
			inverseKey[Byte.toUnsignedInt(index)] = (byte) i;

		}
	}
	

	// constructor will use the byte array parameter as its key
	public Substitute(byte[] bytekey) {
		key = bytekey;
		inverseKey = new byte[256];

		for (int i = 0; i < 256; i++) {
			// convert negative index signed bytes to unsigned integers
			Byte index = new Byte(key[i]);
			inverseKey[Byte.toUnsignedInt(index)] = (byte) i;

		}

	}

	
	@Override
	public byte[] getKey() {
		return key;
	}

	
	// convert the String parameter to an array of bytes, then iterate through all
	// of the bytes,
	// substituting the appropriate bytes from the key. Again, be careful with
	// negative byte values
	@Override
	public byte[] encode(String S) {
		if (S == null)
			throw new IllegalArgumentException();
		
		//System.out.println("-----Sub Encoding-----\n");
		// convert the String parameter to an array of bytes
		byte[] encrypted = S.getBytes();
		byte[] result = new byte[encrypted.length];

		for (int i = 0; i < encrypted.length; i++) {
			Byte index = new Byte(encrypted[i]);
			//result[i] = key[encrypted[i] & Byte.toUnsignedInt(index)];
			result[i] = key[Byte.toUnsignedInt(index)];
		}
		return result;
	}

	
	// reverse the substitution (using your decode byte array) and convert the
	// resulting bytes back to a String.
	@Override
	public String decode(byte[] bytes) {
		if (bytes == null)
			throw new IllegalArgumentException();

		//System.out.print("-----Sub Decoding-----\n ");
		byte[] decodes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			Byte index = new Byte(bytes[i]);
			//decodes[i] = (byte) (inverseKey[bytes[i]] & Byte.toUnsignedInt(index));
			decodes[i] = (byte) (inverseKey[Byte.toUnsignedInt(index)]);
		}

		// Convert the resulting byte array back to a String and return it.
		return new String(decodes);

	}

	
	// for testing purpose
	public static void main(String[] args) {
		String s = "I just need a long string. A text string, also known as a string or simply as text, "
				+ "is a group of characters that are used as data in a spreadsheet program. "
				+ "Text strings are most often comprised of words, but may also include letters, numbers, "
				+ "special characters, the dash symbol, or the number sign. It's more than 300 characters";

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