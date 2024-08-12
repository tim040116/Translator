package etec.framework.encryption.service;

import java.util.Base64;

public class Base64Tool {

	public String encode(String str) {
		return Base64.getEncoder().encodeToString(str.getBytes());
	}
	
	public String decode(String str) {
		return new String(Base64.getDecoder().decode(str));
	}
}
