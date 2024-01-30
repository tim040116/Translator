package etec.src.sql.gp.translater;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.Mark;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;

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
	
	String regDate= "YMDHS:,\\-\\\\\\/ ";//日期格式會用到的字符
	String regInt = "0-9ZI\\.,\\+\\-\\$\\%\\(\\)";//數字格式會用到的字符
	
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
	 * <br> {@link SQLTranslater#changeAddMonths(String)}
	 * <br> {@link SQLTranslater#changeDateFormat(String)}
	 * <br>DATE 轉成 CURRENT_DATE
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String easyReplase(String script) {
		//語法正規化
		String res = script
				.replaceAll("(?i)\\bMINUS\\b", "EXCEPT")//MINUS
				.replaceAll("(?i)\\bSEL\\b", "SELECT")//SEL
				.replaceAll("(?i)\\bNVL\\b", "COALESCE")//NVL
				.replaceAll("(?i)\\bOREPLACE\\s*\\(", "REPLACE\\(")//OREPLACE
				.replaceAll("(?i)\\bSTRTOK\\s*\\(", "SPLIT_PART\\(")//STRTOK
				.replaceAll("(?i)CHARACTERS\\s*\\(","LENGTH\\(")//CHARACTERS
				.replaceAll("(?i)\\bAS\\s+FORMAT\\s+'(["+regDate+"]+)'","AS DATE FORMAT '$1'")//DATE FORMAT 正規化
				.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT","AS DATE FORMAT")//DATE FORMAT 正規化
//				.replaceAll("(?i)(\\(\\s*FORMAT\\s+'[^']+'\\s*\\))\\s*\\((VAR)?CHAR\\s*\\(\\s*\\d+\\s*\\)\\s*\\)", "$1")//FORMAT DATE 語法正規化
//				
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
			t = changeAddMonths(t);
			t = changeLastDay(t);
			t = changeDateFormat(t);
			t = changeTypeConversion(t);
			t = changeFormatNumber(t);
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
		res = changeCurrentDate(res);
		return res;
	}
	/**
	 * <br>INTERVAL可進行月份加減
	 * <br>但會將資料型態轉變為 timestamp
	 * <br>因此需要配合CAST語法進行轉換
	 * <br>
	 * <br>ADD_MONTHS($1,$2) 轉成 CAST($1-INTERVAL'$2 MONTH' AS DATE)
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * 
	 * */
	public String changeAddMonths(String sql) {
		String res = sql;
		res = res
			.replaceAll("(?i)ADD_MONTHS\\s*\\(([^,]+)\\s*,\\s*\\-\\s*(\\d+)\\s*\\)", "CAST\\($1-INTERVAL'$2 MONTH' AS DATE\\)")//ADD_MONTHS
			.replaceAll("(?i)ADD_MONTHS\\s*\\(([^,]+)\\s*,\\s*(\\d+)\\s*\\)", "CAST\\($1+INTERVAL'$2 MONTH' AS DATE\\)")//ADD_MONTHS
			.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)\\s*([\\+\\-]\\s*INTERVAL'[^']+'\\s+AS\\s+DATE)\\s*\\)","AS DATE\\)$2 FORMAT $1\\)")//ADD_MONTH(CAST AS DATE
		;
		return res;
	}
	/**
	 * 
	 * <h1>LAST_DAY 取該月的最後一天</h1>
	 * 
	 * <br> * 此語法目前只提供參數為DATE使用，若為其他格式則須人工轉換資料型態
	 * <br>
	 * <br> 先使用DATE_TRUNC 取該月第一天
	 * <br> 用 INTERVAL 語法 加一個月後減一天
	 * <br> 由於 INTERVAL 語法運算後會轉成 timestamp 所以要再轉回 DATE
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * */
	public String changeLastDay(String sql) {
		String res = sql;
		res = res
			.replaceAll("(?i)LAST_DAY\\(([^\\)]+)\\)", "CAST\\(DATE_TRUNC\\('Month',$1\\)+INTERVAL'1MONTH'-INTERVAL'1DAY' AS DATE\\)")
		;
		return res;
	}
	
	/**
	 * 
	 * 
	 * <br>1. 強制轉換  //2023-12-10 搬遷至changeTypeConversion
	 * <br>		1-1. (FORMAT 'YYYY-MM-DD')(CHAR(7)) 轉成 TO_CHAR($1,'$2')
	 * <br>		1-2. $1(FORMAT '$2') 轉成 TO_DATE($1,'$2')
	 * <br>2. SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成 TO_CHAR($1,'$2')
	 * <br>		2-1. 放棄單獨處理 AS FORMAT 一律於easyReplase()中正規化為 AS DATE FORMAT
	 * <br>		2-2. 先處理日期截取
	 * <br>3. 簡化日期轉字串的語法
	 * <br>		3-1. CAST(CAST($1 AS DATE FORMAT '$2') AS VARCHAR(d+)) 轉成 TO_CHAR($1,$2)
	 * <br>		3-2. CAST(TO_CHAR($1, 'YYYY-MM') AS CHAR(7)) 轉成 TO_CHAR($1, 'YYYY-MM')
	 * <br>		3-3. TO_CHAR(CAST($1 AS DATE FORMAT 'YYYY-MM-DD'), 'YYYY-MM') 轉成 TO_CHAR(CAST($1 AS DATE), 'YYYY-MM')
	 * <br>4. 轉換FORMAT 成 YYYYMMDD 後做加減的語法
	 * <br>		$1 AS DATE FORMAT 'YYYYMMDD')+1 轉成 $1 AS DATE)+1
	 * <br>5. 
	 * 
	 * 
	 * <br>2023/12/06	Tim	因涉及邏輯問題暫時廢棄
	 * <br>2023/12/12	Tim 重啟功能，分層處理
	 * 		
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
//	@Deprecated
	public String changeDateFormat(String sql) {
		String res = sql;
		
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.savelyConvert(sql,(String t)->{
			/**  2.處理日期截取語法
			 * 1. 先將SUBSTR(CAST(AS DATE FORMAT語法轉換成TO_CHAR，FORMAT 跟 SUBSTR 合併
			 * 	SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成 TO_CHAR(CAST($1 AS DATE),SUBSTR($2,$3,$4))
			 * 2.SUBSTR($2,$3,$4)轉成一般字串
			 * */
			t = t
				//步驟2-1
				.replaceAll("(?i)SUBSTR\\s*\\(\\s*CAST\\s*\\(\\s*([^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\),(\\d+),(\\d+)\\)", "TO_CHAR\\(CAST\\($1 AS DATE\\),SUBSTR\\($2,$3,$4\\)\\)")
			;
			//步驟2-2
			Matcher m = Pattern.compile("(?i)SUBSTR\\s*\\(\\s*'([^']+)'\\s*,(\\d+)\\s*,\\s*(\\d+)\\s*\\)",Pattern.CASE_INSENSITIVE).matcher(t);
			while (m.find()) {
				String format = m.group(1);
				int substr1 = Integer.parseInt(m.group(2))-1;
				int substr2 = substr1+Integer.parseInt(m.group(3));
				format = "'"+format.substring(substr1, substr2)+"'";
				t = t.replace(m.group(0),format);
			}
			//步驟3
			t = t
				//3-1  CAST(CAST($1 AS DATE FORMAT '$2') AS VARCHAR(d+))
				.replaceAll("(?i)CAST\\s*\\(\\s*CAST\\s*\\((\\s*[^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)\\s+AS\\s+(VAR)?CHAR\\s*\\(\\s*\\d+\\s*\\)\\s*\\)", "TO_CHAR\\(CAST\\($1 AS DATE\\),$2\\)")
				.replaceAll("(?i)CAST\\s*\\(\\s*CAST\\s*\\((\\s*[^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)\\s+AS\\s+(VAR)?CHAR[^\\)]+\\)", "TO_CHAR\\(CAST\\($1 AS DATE\\),$2\\)")
				//3-2  CAST(TO_CHAR($1, 'YYYY-MM') AS CHAR(7))
				.replaceAll("(?i)CAST\\s*\\(\\s*TO_DATE\\s*\\(\\s*([^\\(\\),]+\\s*,\\s*'[^']+')\\s*\\)\\s*AS\\s+(VAR)?CHAR\\(\\d+\\)\\)", "TO_CHAR\\($1\\)")
				//3-3  TO_CHAR(CAST($1 AS DATE FORMAT 'YYYY-MM-DD'), 'YYYY-MM')
				.replaceAll("(?i)(TO_CHAR\\s*\\(\\s*CAST\\([^\\(\\)]+\\s*AS\\s+DATE)\\s+FORMAT\\s+'[^']+'\\s*\\)", "$1\\)")
				.replaceAll("(?i)TO_CHAR\\s*\\(\\s*CAST\\s*\\(([^\\(\\)]+)\\s+AS+\\s+DATE\\s*\\)\\s*,\\s*('[^']+')\\s*\\)", "TO_CHAR\\($1,$2\\)")
				//4
				.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT\\s+'YYYYMMDD'\\s*\\)\\s*([\\+\\-]\\d+)", "AS DATE\\)$1")
			;
			//清除重複的CAST
			t = t
				.replaceAll("(?i)CAST\\s*\\(CAST\\(([^\\(\\)]+)\\s+AS\\s+DATE\\s*\\)(\\s*[\\+\\-]\\s*(\\d+|INTERVAL\\s*'[^']+'))?\\s*AS\\s+DATE\\s*\\)", "CAST\\($1 AS DATE\\)$2")
			;
			return t;
		});
		return res;
	}
	/**
	 * 
	 * <br>轉換欄位強制轉換的語法
	 * <br>
	 * <br>$1(FORMAT 'YYYY-MM-DD') >> TO_DATE($1,'YYYY-MM-DD')
	 * <br>$1(CHAR(7)) >> cast($1 as char(7))
	 * <br>$1(FORMAT 'YYYY-MM-DD')(CHAR(7)) >> TO_CHAR($1 ,'YYYY-MM-DD')
	 * <br>DATE'${TX4Y-M}-01' >> CAST('${TX4Y-M}-01' AS DATE)
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
	public String changeTypeConversion(String sql) {
		
		String res = sql;
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.savelyConvert(sql, (String t)->{
			t = t
				//1-1 (FORMAT 'YYYY-MM-DD')(CHAR(7))
				.replaceAll("(?i)([.\\w]+)\\s*(\\([^\\)]+\\))?\\s*\\(\\s*FORMAT\\s+('["+regDate+"]+')\\s*\\)\\s*\\(\\s*(VAR)?CHAR\\(\\d+\\)\\s*\\)", "TO_CHAR\\($1$2, $3\\)")
				//1-2 (FORMAT 'YYYY-MM-DD')
				.replaceAll("(?i)([.\\w]+)\\s*(\\([^\\)]+\\))?\\s*\\(\\s*FORMAT\\s+('["+regDate+"]+')\\s*\\)", "TO_DATE\\($1$2, $3\\)")
				//DATE''
				.replaceAll("(?i)\\bDATE\\s*('[^']+')", "CAST\\($1 AS DATE\\)")
			;
			
			t= t
				//CHAR
				.replaceAll("(?i)([\\w\\.]+)(\\([^\\)]+\\))?\\((CHAR<[^>]+>\\d+<[^>]+>)\\)", "CAST\\($1$2 AS $3\\)")
			;
			return t;
		});
		return res;
	}
	/**
	 * CURRENT_DATE 轉換
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * */
	public String changeCurrentDate(String sql) {
		String res = sql;
		res = res
			.replaceAll("(?i)AS(\\s+)DATE", Mark.MAHJONG_BLACK+"$1"+Mark.MAHJONG_WHITE)
			.replaceAll("\\bDATE\\b", "CURRENT_DATE")
			.replaceAll(Mark.MAHJONG_BLACK, "AS")
			.replaceAll(Mark.MAHJONG_WHITE, "DATE")
		;
		return res;
	}
	/**
	 * <h1>轉換CAST轉換數字的語法</h1>
	 * <br>CAST($1 AS FORMAT '9(12)') -> TO_CHAR($1,'000000000000')
	 * 
	 * @author	Tim
	 * @since	2023年12月20日
	 * */
	public String changeFormatNumber(String sql) {
		String res = sql;
		ConvertFunctionsSafely cff = new ConvertFunctionsSafely();
		res = cff.savelyConvert(res, (String t)->{
			Map<String,String> mapIntFormat = new HashMap<String,String>();//需轉換的語法
			mapIntFormat.put("9", "0");
			mapIntFormat.put("Z", "9");
			//先抓出FORMAT語句
			Matcher m = Pattern.compile("(?i)CAST\\(([^\\(\\)]+)\\s+AS\\s+FORMAT\\s+'([^']+)'\\s*\\)",Pattern.CASE_INSENSITIVE).matcher(t);
			while (m.find()) {
				String oldscript = m.group(0);
				String col = m.group(1);//參數
				String fmt = ConvertFunctionsSafely.decodeMark(m.group(2));//格式
				if(!fmt.matches("["+regInt+"]+")) {//確認是否為數字轉換
					return sql;
				}
				//代碼轉換
				for (Map.Entry<String, String> e : mapIntFormat.entrySet()) {
					fmt = fmt.replace(e.getKey(),e.getValue());
				}
				//()攤開
				Matcher m2 = Pattern.compile("(?i)(.)\\((\\d+)\\)",Pattern.CASE_INSENSITIVE).matcher(fmt);
				while (m2.find()) {
					String oldscript2 = m2.group(0);
					String c = m2.group(1);//字符
					int cnt = Integer.parseInt(m2.group(2));//數量
					String newscript2 = "";
					for(int i = 0;i<cnt;i++) {
						newscript2+=c;
					}
					fmt = fmt.replace(oldscript2, newscript2);
				}
				String newscript = "TO_CHAR("+col+",'"+fmt+"')";
				t = t.replace(oldscript, newscript);
			}
			return t;
		});
		return res;
	}
}
