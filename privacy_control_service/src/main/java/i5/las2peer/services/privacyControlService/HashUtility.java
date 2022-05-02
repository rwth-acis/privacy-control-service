package i5.las2peer.services.privacyControlService;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.google.common.hash.Hashing;


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
}
