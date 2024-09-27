package etec.framework.security.encryption.service;

import etec.framework.security.encryption.factory.EncryptionFactory;

public class EncryptionService {

	//base64扣掉最後的==
	public static String decodeA(String str) {
		return EncryptionFactory.base64.decode(str+"==");
	}

	//base64扣掉最後的==
	public static String decodeB(String str) {
		return EncryptionFactory.base64.decode(str);
	}
}
