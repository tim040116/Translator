package main;

import java.text.SimpleDateFormat;
import java.util.Date;

import etec.src.main.ParamsFactory;
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
			ParamsFactory.init();
			String now = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
			//讀檔
			String a = "YYYY-MM-DD";
			int substr1 = Integer.parseInt("9")-1;
			int substr2 = substr1+Integer.parseInt("2");
			a = a.substring(substr1, substr2);
			System.out.println(a);
			//每一個sf的
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
