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
			String text = "	WHEN ( CAST( INSTR(@P_ITEM_LIST,',',1)  AS INTEGER )>0\r\n" + 
					"   AND CAST(INSTR(LOWER(@P_ITEM_LIST),'SELECT')  AS INTEGER)=0 ) \r\n" + 
					"\r\n" + 
					"	WHEN ( CAST( INSTR(@P_ITEM_LIST,',',1)  AS INTEGER )=0  \r\n" + 
					"AND CAST(INSTR(LOWER(@P_ITEM_LIST),'SELECT')  AS INTEGER)=0 ) ";
			text = text.replaceAll("(?i)INSTR\\s*\\(([@A-Za-z0-9_'\\(\\)]+),('[^']+'+)(,[0-9]+)?\\)"
					, "CHARINDEX($2,$1 $3)");

					
			System.out.println(text);
			//每一個sf的
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
