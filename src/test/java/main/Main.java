package main;

import java.text.SimpleDateFormat;
import java.util.Date;

import etec.common.utils.ConvertFunctionsSafely;
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
			String a = "'((REMD_ADD_TKSL('+@P_DAY_ID+',''M'',CAST(T1.ACCU_AMT AS NUMBER),CAST(ACCU_TKSL AS NUMBER))+0)/DECODE(T1.ACCU_PLAN_STNUM,0,NULL,T1.ACCU_PLAN_STNUM))/ '+\r\n" + 
					"'   DECODE(REMD_ADD_TKSL('+@P_DAY_ID+',''M'',CAST(T1.TOT_AMT_LAST_MONTH AS NUMBER),CAST(T1.TOT_TKSL_LAST_MONTH AS NUMBER))/ '+\r\n" + 
					"'   DECODE((CASE WHEN ('+@P_DAY_ID+' < 20100101 OR '+@P_DAY_ID+' > 20101231) THEN T1.TOT_PLAN_STNUM_LAST_MONTH ELSE 0 END),0,NULL,(CASE WHEN ('+@P_DAY_ID+' < 20100101 OR '+@P_DAY_ID+' > 20101231) THEN T1.TOT_PLAN_STNUM_LAST_MONTH ELSE 0 END)), '+\r\n" + 
					"'   0,NULL, REMD_ADD_TKSL('+@P_DAY_ID+',''M'',CAST(T1.TOT_AMT_LAST_MONTH AS NUMBER),CAST(T1.TOT_TKSL_LAST_MONTH AS NUMBER))/ '+\r\n" + 
					"'   DECODE((CASE WHEN ('+@P_DAY_ID+' < 20100101 OR '+@P_DAY_ID+' > 20101231) THEN T1.TOT_PLAN_STNUM_LAST_MONTH ELSE 0 END),0,NULL,(CASE WHEN ('+@P_DAY_ID+' < 20100101 OR '+@P_DAY_ID+' > 20101231) THEN T1.TOT_PLAN_STNUM_LAST_MONTH ELSE 0 END)))*100 '+\r\n" + 
					"'AS R8, ';";
			String script = a
				.replaceAll("\\s*;\\s*$", "")
				.replaceAll("(?<![\\+\\'])'(?![\\+\\'])", "")
				.replaceAll("'{2}?", "'")
				.replaceAll("(?m)'\\s*\\+\\s*$", "")
				.replaceAll("'\\s*$", "")
				.replaceAll("^\\s*\\+\\s*'", "")
			;
			String ca = "'((REMD_ADD_TKSL('+@P_DAY_ID+',''M'',CAST(T1.ACCU_AMT AS NUMBER),CAST(ACCU_TKSL AS NUMBER))+0)/NULLIF(T1.ACCU_PLAN_STNUM,0))/ ' + \r\n" + 
					"'   NULLIF(REMD_ADD_TKSL('+@P_DAY_ID+',''M'',CAST(T1.TOT_AMT_LAST_MONTH AS NUMBER),CAST(T1.TOT_TKSL_LAST_MONTH AS NUMBER))/ ' + \r\n" + 
					"'   NULLIF((CASE WHEN ('+@P_DAY_ID+' < 20100101 OR '+@P_DAY_ID+' > 20101231) THEN T1.TOT_PLAN_STNUM_LAST_MONTH ELSE 0 END),0),0)*100 ' + \r\n" + 
					"'AS R8, ';";
			ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
			String s2 =cfs.savelyConvert(script, (t)->{
				String res = t
					.replaceAll("DECODE\\s*\\(\\s*([^,]+)\\s*,\\s*(\\d+)\\s*,\\s*NULL\\s*,\\s*\\1\\s*\\)", "NULLIF\\($1,$2\\)")
				;
				return res;
			});
			
			
			
			String s3 = s2
				.replaceAll("'", "''")
				.replaceAll("'('\\s*\\+.*?\\s*\\+\\s*')'", "$1")
				.replaceAll("(?m)^", "'")
				.replaceAll("(?m)$", "' + ")
				.replaceAll("'\\s*\\+\\s*$", "';")
			;
			System.out.println(s3);
			//每一個sf的
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

}
