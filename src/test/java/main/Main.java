package main;

import etec.framework.encryption.factory.EncryptionFactory;
import etec.src.security.encryption.DatetimeCondition;
import etec.view.frame.LoginFrame;

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
			new LoginFrame();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
