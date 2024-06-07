package etec.src.sql.gp.translater.service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.Mark;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;

/**
 * <h1>轉換改變資料型態與法</h1>
 * <br>
 * <h2>屬性</h2>
 * <p>static String REG_DATE : 日期格式會用到的字符</p>
 * <p>static String REG_INT : 數字格式會用到的字符</p>
 * <h2>方法</h2>
 * <p>{@link #changeAddMonths(String)}</p>
 * <p>{@link #changeDateFormat(String)}</p>
 * <p>{@link #changeTypeConversion(String)}</p>
 * <p>{@link #changeCurrentDate(String)}</p>
 * <p>{@link #changeFormatNumber(String)}</p>
 * <h2>異動紀錄</h2>
 * <br>2024年2月22日	Tim	建立功能
 * <br>2024年5月15日	Tim	將changeLastDay合併至changeAddMonths
 * <br>2024年5月15日	Tim	將LAST_DAY合併到changeAddMonths
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class DataTypeService {
	
	public static final String REG_DATE= "YMDHS:,\\-\\\\\\/ ";//日期格式會用到的字符
	public static final String REG_INT = "0-9ZI\\.,\\+\\-\\$\\%\\(\\)";//數字格式會用到的字符
	
	/**
	 * <h1>日期加減轉換</h1>
	 * <br>INTERVAL可進行月份加減
	 * <br>但會將資料型態轉變為 timestamp
	 * <br>因此需要配合CAST語法進行轉換
	 * <br>
	 * <br>ADD_MONTHS($1,$2) 轉成 CAST($1-INTERVAL'$2 MONTH' AS DATE)
	 * <br>
	 * <br>2024年5月15日	Tim	將LAST_DAY合併到changeAddMonths
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * */
	public static String changeAddMonths(String sql) {
		String res = sql;
		res = res
			
			.replaceAll("(?i)ADD_(YEAR|MONTH|DAY)\\s*\\(([^,]+)\\s*,\\s*([+-])\\s*(\\d+)\\s*\\)", " CAST\\($2 $3 INTERVAL'$4 $1' AS DATE\\)")//ADD_MONTHS
			.replaceAll("(?i)ADD_(YEAR|MONTH|DAY)\\s*\\(([^,]+)\\s*,\\s*(\\d+)\\s*\\)", "CAST\\($2+INTERVAL'$3 $1' AS DATE\\)")//ADD_MONTHS
			.replaceAll("(?i)\\bAS\\s+DATE\\s+FORMAT\\s+('[^']+')\\s*\\)\\s*([\\+\\-]\\s*INTERVAL'[^']+'\\s+AS\\s+DATE)\\s*\\)","AS DATE\\)$2 FORMAT $1\\)")//ADD_MONTH(CAST AS DATE
		;
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
		res = res.replaceAll("(?i)LAST_DAY\\(([^\\)]+)\\)", "CAST\\(DATE_TRUNC\\('Month',$1\\)+INTERVAL'1MONTH'-INTERVAL'1DAY' AS DATE\\)");
		return res;
	}
	
	/**
	 * 
	 * 
	 * <br>1. 強制轉換  //2023-12-10 搬遷至changeTypeConversion
	 * <br>		1-1. (FORMAT 'YYYY-MM-DD')(CHAR(7)) 轉成 TO_CHAR($1,'$2')
	 * <br>		1-2. $1(FORMAT '$2') 轉成 TO_DATE($1,'$2')
	 * <br>(廢棄)2. SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成TO_CHAR($1,$2)
	 * <br>		2-1. 放棄單獨處理 AS FORMAT 一律於easyReplase()中正規化為 AS DATE FORMAT
	 * <br>		2-2. 先處理日期截取
	 * <br>2. SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成SUBSTR(TO_CHAR($1 AS DATE FORMAT $2),$3,$4)
	 * <br>3. 簡化日期轉字串的語法
	 * <br>		3-1. CAST(CAST($1 AS DATE FORMAT '$2') AS VARCHAR(d+)) 轉成 TO_CHAR($1,$2)
	 * <br>		3-2. CAST(TO_CHAR($1, 'YYYY-MM') AS CHAR(7)) 轉成 TO_CHAR($1, 'YYYY-MM')
	 * <br>		3-3. TO_CHAR(CAST($1 AS DATE FORMAT 'YYYY-MM-DD'), 'YYYY-MM') 轉成 TO_CHAR(CAST($1 AS DATE), 'YYYY-MM')
	 * <br>4. 轉換FORMAT 成 YYYYMMDD 後做加減的語法
	 * <br>		$1 AS DATE FORMAT 'YYYYMMDD')+1 轉成 $1 AS DATE)+1
	 * <br>5. TRUNC(aa, 'MONTH') 轉成  CAST(DATE_TRUNC('MONTH',aa)AS DATE)
	 * <br>6. DATEADD(day, 45, a.Quta_StartDate) 轉成 a.Quta_StartDate + INTERVAL'45 day'
	 * <br>7. CAST(aaa as smallint format '99') -> TRIM(TO_CHAR(aaa,'00'))
	 * <h2>異動紀錄</h2>
	 * <p>2023/12/06	Tim	因涉及邏輯問題暫時廢棄</p>
	 * <p>2023/12/12	Tim 重啟功能，分層處理</p>
	 * <p>2024年2月5日	Tim	為求穩定性，Jason 要求第二項改為SUBSTR(TO_CHAR($1,'YYYY-MM-DD'),9,2)</p>
	 * <br>2024年5月7日	Tim	增加5,6邏輯
	 * @author	Tim
	 * @since	4.0.0.0
	 * */
//	@Deprecated
	public static String changeDateFormat(String sql) {
		String res = sql;
		/**
		 * substr(cast(as date))改成substr(to_char())
		 * */
		res = res
			//步驟2-1
			.replaceAll("(?i)(SUBSTR\\s*\\(\\s*)CAST(\\s*\\(\\s*[^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+'\\s*\\)\\s*,\\s*\\d+\\s*,\\s*\\d+\\s*\\))", "$1TO_CHAR$2,$3")
		;
//			/**  2.處理日期截取語法
//			 * 1. 先將SUBSTR(CAST(AS DATE FORMAT語法轉換成TO_CHAR，FORMAT 跟 SUBSTR 合併
//			 * 	SUBSTR(CAST($1 AS DATE FORMAT $2),$3,$4) 轉成 TO_CHAR(CAST($1 AS DATE),SUBSTR($2,$3,$4))
//			 * 2.SUBSTR($2,$3,$4)轉成一般字串
//			 * 
//			 * <p>2024年2月5日	Tim	應Jason要求更改寫法</p>
//			 * */
//			t = t
//					//步驟2-1
//					.replaceAll("(?i)SUBSTR\\s*\\(\\s*CAST\\s*\\(\\s*([^\\(\\)]+)\\s+AS\\s+DATE\\s+FORMAT\\s+('[^']+')\\),(\\d+),(\\d+)\\)", "TO_CHAR\\(CAST\\($1 AS DATE\\),SUBSTR\\($2,$3,$4\\)\\)")
//				;
//			//步驟2-2
//			Matcher m = Pattern.compile("(?i)SUBSTR\\s*\\(\\s*'([^']+)'\\s*,(\\d+)\\s*,\\s*(\\d+)\\s*\\)",Pattern.CASE_INSENSITIVE).matcher(t);
//			while (m.find()) {
//				String format = m.group(1);
//				int substr1 = Integer.parseInt(m.group(2))-1;
//				int substr2 = substr1+Integer.parseInt(m.group(3));
//				format = "'"+format.substring(substr1, substr2)+"'";
//				t = t.replace(m.group(0),format);
//			}
		//步驟3
		res = res
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
			//5
			.replaceAll("(?i)\\bTRUNC\\s*\\(([^,]+),\\s*('YEAR|MONTH|DAY')\\s*\\)", "CAST\\(DATE_TRUNC\\($2,$1\\) AS DATE\\)")
			//6
			.replaceAll("(?i)\\bDATEADD\\s*\\(([^,]+),\\s*(-?\\d+)\\s*,([^)]+)\\)", "CAST\\($3 + INTERVAL'$2 $1' AS DATE\\)")
		;
////		//清除重複的CAST
//		res = res
//			.replaceAll("(?i)CAST\\s*\\(CAST\\(([^\\(\\)]+)\\s+AS\\s+DATE\\s*\\)(\\s*[\\+\\-]\\s*(\\d+|INTERVAL\\s*'[^']+'))?\\s*AS\\s+DATE\\s*\\)", "CAST\\($1 AS DATE\\)$2")
//		;
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
	public static String changeTypeConversion(String sql) {
		
		String res = sql;
		res = res
			//1-1 (FORMAT 'YYYY-MM-DD')(CHAR(7))
//			.replaceAll("(?i)([.\\w]+)\\s*(\\([^\\)]+\\))?\\s*\\(\\s*FORMAT\\s+('["+REG_DATE+"]+')\\s*\\)\\s*\\(\\s*(VAR)?CHAR\\(\\d+\\)\\s*\\)", "TO_CHAR\\($1$2, $3\\)")
			//1-2 (FORMAT 'YYYY-MM-DD')
			.replaceAll("(?i)([.\\w]+)\\s*(\\([^\\)]+\\))?\\s*\\(\\s*FORMAT\\s+('["+REG_DATE+"]+')\\s*\\)", "TO_DATE\\($1$2, $3\\)")
			//DATE''
			.replaceAll("(?i)\\bDATE\\s*('[^']+')", "CAST\\($1 AS DATE\\)")
			//CHAR
			.replaceAll("(?i)([\\w\\.]+)(\\([^\\)]+\\))?\\((CHAR<[^>]+>\\d+<[^>]+>)\\)", "CAST\\($1$2 AS $3\\)")
		;
		return res;
	}
	/**
	 * CURRENT_DATE 轉換
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * 
	 * */
	public static String changeCurrentDate(String sql) {
		String res = sql;
		res = res
			.replaceAll("(?i)AS(\\s+)DATE", Mark.MAHJONG_BLACK+"$1"+Mark.MAHJONG_WHITE)
			.replaceAll("(?i)\\bDATE\\b", "CURRENT_DATE")
			.replaceAll(Mark.MAHJONG_BLACK, "AS")
			.replaceAll(Mark.MAHJONG_WHITE, "DATE")
		;
		return res;
	}
	/**
	 * <h1>轉換CAST轉換數字的語法</h1>
	 * <br>CAST($1 AS FORMAT '9(12)') -> TRIM(TO_CHAR($1,'000000000000'))
	 * 
	 * 2024年6月4日	Tim	加上TRIM
	 * @author	Tim
	 * @since	2023年12月20日
	 * */
	public static String changeFormatNumber(String sql) {
		String res = sql;
		Map<String,String> mapIntFormat = new HashMap<String,String>();//需轉換的語法
		mapIntFormat.put("9", "0");
		mapIntFormat.put("Z", "9");
		//先抓出FORMAT語句
//		String reg = "(?i)CAST\\(([^\\(\\)]+)\\s+AS\\s+(?:[\\w+,()]+\\s+)?FORMAT\\s+'([^']+)'\\s*\\)";
		String reg = "(?i)CAST\\(([^\\(\\)]+)\\s+AS\\s+FORMAT\\s+'([^']+)'\\s*\\)";
		Matcher m = Pattern.compile(reg,Pattern.CASE_INSENSITIVE).matcher(res);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String col = m.group(1);//參數
			String fmt = ConvertFunctionsSafely.decodeMark(m.group(2));//格式
			if(!fmt.matches("["+REG_INT+"]+")) {//確認是否為數字轉換
				return sql;
			}
			//代碼轉換
			fmt = formatConvert(fmt);
			//()攤開
			Matcher m2 = Pattern.compile("(?i)(.)\\((\\d+)\\)",Pattern.CASE_INSENSITIVE).matcher(fmt);
			StringBuffer sb2 = new StringBuffer();
			while (m2.find()) {
				String c = m2.group(1);//字符
				int cnt = Integer.parseInt(m2.group(2));//數量
				String newscript2 = "";
				for(int i = 0;i<cnt;i++) {
					newscript2+=c;
				}
				m2.appendReplacement(sb2, newscript2);
			}
			m2.appendTail(sb2);
			m.appendReplacement(sb, "TO_CHAR("+col+",'"+sb2+"')");
//			m.appendReplacement(sb, Matcher.quoteReplacement("TRIM(TO_CHAR("+col+",'"+sb2+"'))"));
		}
		res =  m.appendTail(sb).toString();
		return res;
	}
	
	/**
	 * <h1>轉換next_day</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月7日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	e
	 * @see		
	 * @return	return_type
			 */
	public static String changeNextDay(String sql) {
		if(!sql.toUpperCase().contains("NEXT_DAY")) {
			return sql;
		}
		//week
		Map<String,String> mapWeek = new HashMap<String,String>();
		mapWeek.put("SU","0");
		mapWeek.put("SUN","0");
		mapWeek.put("SUNDAY","0");
		mapWeek.put("M","1");
		mapWeek.put("MO","1");
		mapWeek.put("MON","1"); 
		mapWeek.put("MONDAY","1");
		mapWeek.put("TU","2");
		mapWeek.put("TUE","2");
		mapWeek.put("TUES","2");
		mapWeek.put("TUESDAY","2");
		mapWeek.put("W","3");
		mapWeek.put("WE","3");
		mapWeek.put("WED","3");
		mapWeek.put("WEDNESDAY","3");
		mapWeek.put("TH","4");
		mapWeek.put("THU","4");
		mapWeek.put("THURS","4");
		mapWeek.put("THURSDAY","4");
		mapWeek.put("F","5");
		mapWeek.put("FR","5");
		mapWeek.put("FRI","5");
		mapWeek.put("FRIDAY","5");
		mapWeek.put("SA","6");
		mapWeek.put("SAT","6");
		mapWeek.put("SATURDAY","6");
		String res = "";
		
		/**
		 * <p>功能 ：轉換 NEXT_DAY</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：i</p>
		 * <p>範圍 ：從  NEXT_DAY 到 )</p>
		 * <h2>群組 ：</h2>
		 * 	1.日期格式的資料
		 *  2.星期幾，參照上方的mapWeek
		 * <h2>備註 ：</h2>
		 * 	
		 * <h2>異動紀錄 ：</h2>
		 * 2024年5月8日	Tim	建立邏輯
		 * */
		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile("(?i)\\bNEXT_DAY\\s*\\(([^,]+),\\s*'(\\w+)'\\s*\\)");
		Matcher m = p.matcher(sql);
		while (m.find()) {
			String col  = m.group(1);
			String week = mapWeek.get(m.group(2).toUpperCase());
			String newsql = col + " + ((7 - EXTRACT(DOW FROM " + col + ") + " + week + ")::int%7)";
			m.appendReplacement(sb, Matcher.quoteReplacement(newsql));
		}
		m.appendTail(sb);
		res =  sb.toString();
		return res;
	}
	
	private static String formatConvert(String format) {
		Map<String,String> mapIntFormat = new HashMap<String,String>();//需轉換的語法
		mapIntFormat.put("9", "0");
		mapIntFormat.put("Z", "9");
		for (Map.Entry<String, String> e : mapIntFormat.entrySet()) {
			format = format.replace(e.getKey(),e.getValue());
		}
		return format;
	}
}







