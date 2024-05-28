package etec.src.sql.gp.translater;

import etec.common.exception.sql.SQLFormatException;
import etec.common.utils.Mark;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;
import etec.common.utils.log.Log;
import etec.src.sql.gp.translater.service.DataTypeService;
import etec.src.sql.gp.translater.service.UnpivotService;

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
	 * <br>average改成AVG
	 * <br>CHARACTERS|CHAR|CHARACTER_LENGTH|LENGTH改成LENGTH
	 * <br>INSTR改成POSITION
	 * <br>ISNULL改成COALESCE
	 * <br>LIKE ANY('','','') 轉成 LIKE ANY(ARRAY['','',''])	
	 * <br>日期運算轉換 
	 * <br> {@link DataTypeService#changeAddMonths(String)}
	 * <br> {@link DataTypeService#changeDateFormat(String)}
	 * <br>DATE 轉成 CURRENT_DATE
	 * <br>UNPIVOT語法轉換
	 * <br> {@link UnpivotService#changeTD_UNPIVOT(String)}
	 * <br>	{@link UnpivotService#changeUNPIVOT(String)}
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月2日	Tim	建立功能
	 * <br>2024年5月2日	Tim	開通轉換日期 6 CURRENT_DATE
	 * <br>2024年5月7日	Tim	增加AVG,Character,INSTR,ISNULL
	 * @author	Tim
	 * @throws SQLFormatException 
	 * @since	4.0.0.0
	 * @see	DataTypeService
	 * @see	UnpivotService
	 * */
	public String easyReplase(String script) throws SQLFormatException {
		Log.debug("正規化");
		//語法正規化
		String res = script
				.replaceAll("(?i)\\bMINUS\\b", "EXCEPT")//MINUS
				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
				.replaceAll("(?i)\\bNVL\\b", "COALESCE")//NVL
				.replaceAll("(?i)\\bISNULL\\s*\\(", "COALESCE\\(")//ISNULL
				.replaceAll("(?i)\\bOREPLACE\\s*\\(", "REPLACE\\(")//OREPLACE
				.replaceAll("(?i)\\bSTRTOK\\s*\\(", "SPLIT_PART\\(")//STRTOK
				.replaceAll("(?i)\\b(?:CHARACTERS|CHAR|CHARACTER_LENGTH|LENGTH)\\s*\\(\\s*+(?!\\d)","LENGTH\\(")//LENGTH
				.replaceAll("(?i)\\bAVERAGE\\s*\\(","AVG\\(")//AVERAGE
				.replaceAll("(?i)\\bAS\\s+FORMAT\\s+'(["+DataTypeService.REG_DATE+"]+)'","AS DATE FORMAT '$1'")//DATE FORMAT 正規化
				.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT","AS DATE FORMAT")//DATE FORMAT 正規化 多空白為一個空白
				.replaceAll("(?i)ADD_MONTHS", "ADD_MONTH")//ADD_MONTHS
//				.replaceAll("(?i)(\\(\\s*FORMAT\\s+'[^']+'\\s*\\))\\s*\\((VAR)?CHAR\\s*\\(\\s*\\d+\\s*\\)\\s*\\)", "$1")//FORMAT DATE 語法正規化
//				.replaceAll("(?<!')0\\d+(?!'|\\d)", "'$0'")//0字頭的數字要包在字串裡
				;
		res = changeIn(res);
		Log.debug("第一階段轉換");
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.savelyConvert(res, (String t)->{
			t = t
				.replaceAll("(?i)INDEX\\s*\\(([^,]+),([^\\)]+)\\)", "POSITION\\($2 IN $1\\)")//INDEX改成POSITION
				.replaceAll("(?i)ZEROIFNULL\\s*\\(([^\\)]+)\\)", "COALESCE\\($1,0\\)")//ZEROIFNULL改成COALESCE
				.replaceAll("(?i)NULLIFZERO\\s*\\(([^\\)]+)\\)", "NULLIF\\($1,0\\)")//NullIfZero改成NULLIF
				.replaceAll("(?i)\\bINSTR\\s*\\(([^,]+),([^\\)]+)\\)", "POSITION\\($2 IN $1\\)")//INSTR
				.replaceAll("(?i)LIKE\\s+(ANY|ALL)\\s*\\(\\s*('[^']+'(\\s*\\,\\s*'[^']+')+)\\s*\\)", "LIKE $1 \\(ARRAY[$2])")//LIKE ANY('','','') >> LIKE ANY(ARRAY['','',''])	
			;
			Log.debug("第二階段轉換：日期及數字轉換");
			Log.debug("\t轉換日期 1：日期加減");
			t = DataTypeService.changeAddMonths(t);
			Log.debug("\t轉換日期 2：格式轉換");
			t = DataTypeService.changeDateFormat(t);
			Log.debug("\t轉換日期 3：型態轉換");
			t = DataTypeService.changeTypeConversion(t);
			Log.debug("\t轉換日期 4：下週轉換");
			t = DataTypeService.changeNextDay(t);
			Log.debug("\t轉換數字 1：格式轉換");
			t = DataTypeService.changeFormatNumber(t);
			return t;
		});
//		Log.debug("第二階段轉換：日期及數字轉換");
//		res = cff.savelyConvert(res, (String t)->{
//			Log.debug("\t轉換日期 1：日期加減");
//			t = DataTypeService.changeAddMonths(t);
//			Log.debug("\t轉換日期 2：格式轉換");
//			t = DataTypeService.changeDateFormat(t);
//			Log.debug("\t轉換日期 3：型態轉換");
//			t = DataTypeService.changeTypeConversion(t);
//			Log.debug("\t轉換日期 4：當日轉換");
//			t = DataTypeService.changeCurrentDate(t);
//			Log.debug("\t轉換日期 5：下週轉換");
//			t = DataTypeService.changeNextDay(t);
//			Log.debug("\t轉換數字 1：格式轉換");
//			t = DataTypeService.changeFormatNumber(t);
//			return t;
//		});
		Log.debug("第三階段轉換");
		//須隔離處裡的項目
		res = cff.savelyConvert(res, (String t)->{
			t = t
				//CAST($1 AS DATE FORMAT 'YYYY-MM-DD') 轉成 TO_DATE
				.replaceAll("(?i)CAST\\s*\\(([^\\(\\)]+)\\s*AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)", "TO_DATE\\($1,$2\\)")
			;
			return t;
		});
		Log.debug("第四階段轉換");
		res = DataTypeService.changeCurrentDate(res);
		res = UnpivotService.changeTD_UNPIVOT(res);
		res = UnpivotService.changeUNPIVOT(res);
		return res;
	}
	/**
	 * <h1>IN語法轉換</h1>
	 * <p>僅限單一值的情況下，IN語法可以省略括號</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月28日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	
	 * @throws	
	 * @see		
	 * @return	String
	 */
	public String changeIn(String sql){
		String res = sql.replaceAll("''", Mark.MAHJONG_BLACK+Mark.MAHJONG_BLACK+Mark.MAHJONG_BLACK);
		res = res.replaceAll("(?i)\\bIN\\s*([^\\s(']\\S+|'[^']+')", "IN \\($1\\)");
		res = res.replaceAll(Mark.MAHJONG_BLACK+Mark.MAHJONG_BLACK+Mark.MAHJONG_BLACK, "''")
		;
		
		return res;
	}

}
