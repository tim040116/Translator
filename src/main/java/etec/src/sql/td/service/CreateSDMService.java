package etec.src.sql.td.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.Mark;

/**
 * <h1>產製SDM</h1>
 * <p> 取出SQL中CREATE TABLE 語法
 * <br>並製作成SDM文件
 * <br>
 * <br>
 * <br>
 * </p>
 * <h2>屬性</h2>
 * <p></p>
 * <h2>方法</h2>
 * <p></p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年3月11日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class CreateSDMService {
	
	/**
	 * <h1>產製SDM</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月11日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	fileName	檔案名稱
	 * @param	script		程式碼
	 * @throws	
	 * @see
	 * @return	void	尚未決定是否要返回
	 * */
	public static void createSDM(String fileName,String script) {
		//會出現create table 的情境有兩種
		//第一步要先清除註解
		//第二步要找出單純的Create multiset|set table語法，
		//最後要處理Create table as table
		
		/**
		 * <p>功能 ：清除註解</p>
		 * <p>類型 ：取代</p>
		 * <p>修飾詞：gmi</p>
		 * <h2>備註 ：</h2>
		 * 	1.清除//跟--類型的註解
		 * 	2.將 *\/ 轉換成標記符號
		 *  3.刪除從/*到標記符號的內容
		 * <h2>異動紀錄 ：</h2>
		 * 2024年3月11日	Tim	建立邏輯
		 * */
		script = script.replaceAll("(?://|--).*", "");
		script = script.replaceAll("\\*/", Mark.MAHJONG_BLACK)
				.replaceAll("/*[^"+Mark.MAHJONG_BLACK+"]+"+Mark.MAHJONG_BLACK, "");
		/**
		 * <p>功能 ：取得CREATE table 語法</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：gmi</p>
		 * <p>範圍 ：從 create table 到 ;</p>
		 * <h2>群組 ：</h2>
		 * 	1.MULTISET 或 SET 或 空白
		 * 	2.Table Name
		 * 	3.column script
		 * 	4.primary index 的 column
		 * <h2>備註 ：</h2>
		 * 	
		 * <h2>異動紀錄 ：</h2>
		 * 2024年3月11日	Tim	建立邏輯
		 * */
		Pattern p = Pattern.compile("CREATE\\s+(?:(MULTISET|SET)\\s+)?TABLE\\s+(\\S+)\\s+[^\\(]+\\(([^;]+)\\)\\s*(?:PRIMARY\\s+INDEX\\s*\\(([^\\)]+)\\));",Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(script);
		while(m.find()) {
			String setType	 = m.group(1);//Set or Multiset
			String tableName = m.group(2);//target table name
			String context	 = m.group(3);//other script
			String primaryIndex = m.group(4);//primary index
			if(context.matches("?iAS.*")) {
				continue;
			}
			//切分欄位
			String[] arrCol = context.split(",(?!\\d+\\))");
			
		}
	}
	
}
