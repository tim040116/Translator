package main;

import etec.common.utils.charset.CharsetTool;

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
			String f_utf8 = "C:\\Users\\user\\Desktop\\Trans\\Target\\test_utf8.pl";
			String f_big5 = "C:\\Users\\user\\Desktop\\Trans\\Target\\test_big5.pl";
			String f_gbk = "C:\\Users\\user\\Desktop\\Trans\\Target\\test_GBK.pl";
			System.out.println(CharsetTool.getCharset(f_utf8).name());
			System.out.println(CharsetTool.getCharset(f_big5).name());
			System.out.println(CharsetTool.getCharset(f_gbk).name());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
