package etec.src.security.login.service;

import etec.framework.security.encryption.factory.EncryptionFactory;

public class CreateKeyFileService {

	public void print() {
		String hash = EncryptionFactory.base64.encode("etec/abc123");
		String res = EncryptionFactory.base64.encode("fmi_i/p-eps_abc123-fmd_yyyy/mm/dd_ei_etec-md_2026/12/31-hash_"+hash);
		System.out.println(res);
		System.out.println(EncryptionFactory.base64.decode("eXl5eS9NTS9kZA"));
	}
}
