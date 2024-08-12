package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.encryption.factory.EncryptionFactory;
import etec.src.security.encryption.DatetimeCondition;

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
			DatetimeCondition dc = new DatetimeCondition();
			dc.check(null);
			System.out.println(EncryptionFactory.base64.encode("權限已過期，請聯絡工程師"));
			System.out.println(EncryptionFactory.base64.encode("2026/12/31"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
