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
			String text = 
					 "	CAST(C.L_WEEK_ID	AS INTEGER) AS L_WEEK_ID, "
					+" DECODE(NLV(A.UPLOAD_STNUM,0),0,0, PMART.DBO.DIVIDE_BY_ZERO(CAST(A.AMT AS DECIMAL(18,6)) ,A.UPLOAD_STNUM)) AS AMT, "
					+" DECODE(NLV(A.UPLOAD_STNUM,0),0,0, PMART.DBO.DIVIDE_BY_ZERO(CAST(A.CUST_NUM AS DECIMAL(18,6)) ,A.UPLOAD_STNUM)) AS CUST_NUM, "
					+ " AS P_AMT, ";
//			text = text.replaceAll(
//					 "(?i)INSTR\\s*\\(([@A-Za-z0-9_'\\(\\)]+),('[^']+'+)(,[0-9]+)?\\)"
//					, "CHARINDEX($2,$1 $3)"
//				);
			
					
			System.out.println(text);
			//每一個sf的
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
