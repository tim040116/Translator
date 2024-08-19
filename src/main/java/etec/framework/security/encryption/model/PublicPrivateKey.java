package etec.framework.security.encryption.model;

import java.security.KeyPair;

public class PublicPrivateKey {

	private String publicKey;
	
	private String privateKey;

	public PublicPrivateKey(KeyPair kp) {
		publicKey  = kp.getPublic().toString();
		privateKey = kp.getPrivate().toString();
		
	}
	
	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	
}
