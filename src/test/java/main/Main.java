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
			String text = " ,coalesce(zeroifnull(sum(case when Dim_Data_D='01' then Mea_Txn_Point else 0 END)),\"\") as P01\r\n" + 
					" \r\n" + 
					" ,zeroifnull(sum(case when Dim_Data_D='02' then Mea_Txn_Point else 0 END)) as P02 \r\n" + 
					" \r\n" + 
					"  ,zeroifnull(asasasas) as P02 ";
			text = text.replaceAll("(?i)EXTRACT\\s*\\(\\s*(DAY|MONTH|YEAR)\\s+FROM", "DatePart($1 ,");

					
			System.out.println(text);
			//每一個sf的
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
