import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import node.Security;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			for(int i=0;i<4;i++) {
				KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
				KeyPair kp = gen.generateKeyPair();
				PublicKey pubK = kp.getPublic();
				PrivateKey privK = kp.getPrivate();
				String pKey = Base64.getEncoder().encodeToString(pubK.getEncoded());
				System.out.println(pKey);
				
			}
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
