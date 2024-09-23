package main;

import etec.framework.security.encryption.factory.EncryptionFactory;
import etec.src.security.login.review.LoginReviewer;
import etec.src.security.login.view.LoginFrame;
import etec.src.translator.sql.az.translater.SQLTranslater;
import etec.src.translator.view.application.FamilyMartApplication;

/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";
	
	public static void main(String[] args) {
		try {
//			LoginFrame frame = new LoginFrame(new FamilyMartApplication(),new LoginReviewer());
//			frame.setVisible(true);
			String hash = EncryptionFactory.base64.encode("etec/abc123");
			String res = EncryptionFactory.base64.encode("fmi_i/p-eps_abc123-fmd_yyyy/mm/dd_ei_etec-md_2026/12/31-hash_"+hash);
			System.out.println(res);
			System.out.println(EncryptionFactory.base64.decode("eXl5eS9NTS9kZA"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
