package etec.framework.permission.service;

import etec.framework.encryption.factory.EncryptionFactory;

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
