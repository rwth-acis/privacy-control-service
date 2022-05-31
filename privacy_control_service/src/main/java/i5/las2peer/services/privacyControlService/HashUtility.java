package i5.las2peer.services.privacyControlService;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import i5.las2peer.registry.Util;


public class HashUtility {
	public static String hash(String input) {
		String hashed = Hashing.sha256()
				.hashString(input, StandardCharsets.UTF_8)
				.toString();
		return hashed;
	}
	
	public static String hashWithRandomSalt(String input) {
		Random randomGen = new Random();
		String hashed = Hashing.sha256()
				.hashString(input + randomGen.nextInt(), StandardCharsets.UTF_8)
				.toString();
		return hashed;
	}
	
	public static String hash(String input, String salt) {
		String hashed = Hashing.sha256()
				.hashString(input + salt, StandardCharsets.UTF_8)
				.toString();
		return hashed;
	}
	
	public static byte[] hashForBlockchainByte(String input) {
		byte[] hashed = Hashing.sha256()
				.hashString(input, StandardCharsets.UTF_8)
				.asBytes();
		return hashed;
	}
	
	public static boolean compareHashBytes(byte[] first, byte[] second) {
		HashCode hc1 = HashCode.fromBytes(first);
		HashCode hc2 = HashCode.fromBytes(second);
		if (hc1.equals(hc2)) {
			return true;
		}
		else {
			return false;
		}
	}
}
