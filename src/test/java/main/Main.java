package main;

import java.text.SimpleDateFormat;
import java.util.Date;

import etec.main.ParamsFactory;
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
			String text = "PREPARE Store_Sql FROM SqlStr;";
			text = text
					.replaceAll("PREPARE\\s(\\S+)\\sFROM\\s+(\\S+)\\s*;"
							, "set @SqlCur = N'DECLARE $1 CURSOR FOR ' + @$2 ;\r\n\tEXECUTE sp_executesql @SqlCur")
					;
					
			System.out.println(text);
			//每一個sf的
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
