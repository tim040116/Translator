package etec.framework.security.encryption.utils;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import etec.framework.security.encryption.model.PublicPrivateKey;

public class RSATool {

	public PublicPrivateKey getKey() throws NoSuchAlgorithmException {
		PublicPrivateKey m;
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(2048, new SecureRandom());
		m = new PublicPrivateKey(kpg.generateKeyPair());
		return m;
	}


}
