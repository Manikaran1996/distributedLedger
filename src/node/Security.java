package node;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Security {
	
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private String nodeName;
	
	public Security() {
		privateKey = null;
		publicKey = null;
	}
	
	public Security(String nodeName) {
		this.nodeName = nodeName;
	}
	
	public byte[] getHash(String msg) {
		byte[] hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			hash = md.digest(msg.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return hash;
	}
	
	private void generateKeyPair() {
		try {
			KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
			KeyPair kp = gen.generateKeyPair();
			privateKey = kp.getPrivate();
			publicKey = kp.getPublic();
		
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getPrivateKey() {
		if(privateKey == null) {
			generateKeyPair();
		}
		return bytesToString(privateKey.getEncoded()); //earlier toString was returning extra information than the key itself 
	}
	
	public String getPublicKey() {
		if(publicKey == null) {
			generateKeyPair();
		}
		return bytesToString(publicKey.getEncoded()); //earlier toString was returning extra information than the key itself
	}
	
	private byte[] encrypt (byte[] plainText) {
		byte[] cipherTextBytes = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			cipherTextBytes = cipher.doFinal(plainText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cipherTextBytes;
	}
	
	private byte[] decrypt(byte[] cipherText) {
		byte[] plainTextBytes = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			plainTextBytes = cipher.doFinal(cipherText);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return plainTextBytes;
		
	}
	
	public byte[] createDigest(String msg) {
		byte[] hash = getHash(msg);
		byte[] encryptedHash = encrypt(hash);
		return encryptedHash;
	}
	
	public boolean verifySignature(String msg, byte[] signature) {
		byte[] hash = decrypt(signature);
		byte[] msgHash = getHash(msg);
		if(bytesToString(hash).equals(bytesToString(msgHash)))
			return true;
		return false;
	}
	
	public static String bytesToString(byte[] msg) {
		StringBuffer buffer = new StringBuffer("");
		for(int i=0;i<msg.length;i++) {
			buffer.append(Integer.toString((msg[i]&0xff),16));
		}
		
		return buffer.toString();
	}
	
	//TODO
	public boolean saveKey() {
		String path = "/home/manikaran/M.tech/Software Lab/Nodes/" + nodeName;
		File dir = new File(path);
		boolean created = false;
		if(!dir.exists())
			created = dir.mkdirs();
		
		return true;
	}
	public static void main(String[] args) {
		Security testObject = new Security();
		String msg = "Hello, this is the trial msg";
		byte[] hash = testObject.getHash(msg);
		String msgHash = testObject.bytesToString(hash);
		testObject.generateKeyPair();
		System.out.println("This is the message hash : " + msgHash);
		byte[] cipherText = testObject.encrypt(hash);
		String encryptedHash = testObject.bytesToString(cipherText);
		System.out.println("This is the encrypted hash : " + encryptedHash);
		String decryptedHash = testObject.bytesToString(testObject.decrypt(cipherText));
		System.out.println("This is the decrypted hash : " + decryptedHash);
		if(testObject.verifySignature(msg, cipherText)) {
			System.out.println("Authenticated Message");
		}
		else {
			System.out.println("Message not authenticated");
		}
	}
}
