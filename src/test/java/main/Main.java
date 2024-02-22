package main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.convert_safely.ConvertRemarkSafely;
import etec.src.sql.gp.translater.GreemPlumTranslater;
/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\familymart\\T1\\SQLAExport.txt";
	
	public static void main(String[] args) {
		try {
			String now = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
			
			//()攤開
//			Pattern p1 = Pattern.compile("A");
//			Matcher m1 = p1.matcher("A");
//			System.out.println(m1.group());
//			Matcher m2 = Pattern.compile("A",Pattern.CASE_INSENSITIVE).matcher("123");
//			System.out.println(m2.group());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
