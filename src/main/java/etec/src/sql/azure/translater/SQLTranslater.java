package etec.src.sql.azure.translater;

import etec.common.exception.sql.SQLFormatException;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;
import etec.src.sql.azure.translater.service.DataTypeService;
import etec.src.sql.azure.translater.service.UnpivotService;

/**
 * 
 * 
 * td 轉 gp 通用語法轉換
 * 
 * <h2>常見TD語法</h2>
 * <br>column_name(CHAR(8))	TD語法支援強制轉換資料型態
 * 
 * 
 * <br><br><h2>常見GP語法</h2>
 * <br>INTERVAL	時間間隔，為一種資料型態，用於時間運算，並可以藉由後綴詞限制儲存內容
 * 
 * @author	Tim
 * @since	4.0.0.0
 * @version	4.0.0.0
 * */
public class SQLTranslater {
	
	/**
	 * 
	 * 
	 * <h1>轉換SQL語法</h1><br>
	 * 
	 * <br>NVL 轉成 COALESCE
	 * <br>MINUS 轉成 EXCEPT
	 * <br>SEL 轉成 SELECT
	 * <br>OREPLACE 轉成 REPLACE
	 * <br>STRTOK 轉成 SPLIT_PART
	 * <br>DATE FORMAT 正規化
	 * <br>INDEX改成POSITION
	 * <br>ZEROIFNULL改成COALESCE
	 * <br>IN後面一定要有括號
	 * <br>NullIfZero改成NULLIF
	 * <br>LIKE ANY('','','') 轉成 LIKE ANY(ARRAY['','',''])	
	 * <br>日期運算轉換 
	 * <br> {@link DataTypeService#changeAddMonths(String)}
	 * <br> {@link DataTypeService#changeDateFormat(String)}
	 * <br>DATE 轉成 CURRENT_DATE
	 * <br>UNPIVOT語法轉換
	 * <br> {@link UnpivotService#changeTD_UNPIVOT(String)}
	 * <br>	{@link UnpivotService#changeUNPIVOT(String)}
	 * 
	 * @author	Tim
	 * @throws SQLFormatException 
	 * @since	4.0.0.0
	 * @see	DataTypeService
	 * @see	UnpivotService
	 * */
	public String easyReplase(String script) throws SQLFormatException {
		//語法正規化
		String res = script
				.replaceAll("(?i)\\bMINUS\\b", "EXCEPT")//MINUS
				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
				.replaceAll("(?i)\\bNVL\\b", "COALESCE")//NVL
				.replaceAll("(?i)\\bOREPLACE\\s*\\(", "REPLACE\\(")//OREPLACE
				.replaceAll("(?i)\\bSTRTOK\\s*\\(", "SPLIT_PART\\(")//STRTOK
				.replaceAll("(?i)CHARACTERS\\s*\\(","LENGTH\\(")//CHARACTERS
				.replaceAll("(?i)\\bAS\\s+FORMAT\\s+'(["+DataTypeService.REG_DATE+"]+)'","AS DATE FORMAT '$1'")//DATE FORMAT 正規化
				.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT","AS DATE FORMAT")//DATE FORMAT 正規化
//				.replaceAll("(?i)(\\(\\s*FORMAT\\s+'[^']+'\\s*\\))\\s*\\((VAR)?CHAR\\s*\\(\\s*\\d+\\s*\\)\\s*\\)", "$1")//FORMAT DATE 語法正規化
//				.replaceAll("(?<!')0\\d+(?!'|\\d)", "'$0'")//0字頭的數字要包在字串裡
				;
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.savelyConvert(res, (String t)->{
			t = t
				.replaceAll("(?i)INDEX\\s*\\(([^,]+),([^\\)]+)\\)", "POSITION\\($2 IN $1\\)")//INDEX改成POSITION
				.replaceAll("(?i)ZEROIFNULL\\s*\\(([^\\)]+)\\)", "COALESCE\\($1,0\\)")//ZEROIFNULL改成COALESCE
				.replaceAll("(?i)\\bIN\\s+(?<n1>'[^']+'(,'[^']+')+)", "IN \\(${n1}\\)")//IN後面一定要有括號
				.replaceAll("(?i)NULLIFZERO\\s*\\(([^\\)]+)\\)", "NULLIF\\($1,0\\)")//NullIfZero改成NULLIF
				.replaceAll("(?i)LIKE\\s+ANY\\s*\\(\\s*('[^']+'(\\s*\\,\\s*'[^']+')+)\\s*\\)", "LIKE ANY \\(ARRAY[$1])")//LIKE ANY('','','') >> LIKE ANY(ARRAY['','',''])	
			;
			return t;
		});
		res = cff.savelyConvert(res, (String t)->{
			t = DataTypeService.changeAddMonths(t);
			t = DataTypeService.changeLastDay(t);
			t = DataTypeService.changeDateFormat(t);
			t = DataTypeService.changeTypeConversion(t);
			t = DataTypeService.changeFormatNumber(t);
			return t;
		});
		//須隔離處裡的項目
		res = cff.savelyConvert(res, (String t)->{
			t = t
				//CAST($1 AS DATE FORMAT 'YYYY-MM-DD') 轉成 TO_DATE
				.replaceAll("(?i)CAST\\s*\\(([^\\(\\)]+)\\s*AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)", "TO_DATE\\($1,$2\\)")
			;
			return t;
		});
		res = DataTypeService.changeCurrentDate(res);
		UnpivotService us = new UnpivotService();
		res = us.changeTD_UNPIVOT(res);
		res = us.changeUNPIVOT(res);
		return res;
	}
	

}
