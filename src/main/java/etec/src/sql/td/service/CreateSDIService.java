package etec.src.sql.td.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.FileTool;
import etec.common.utils.Mark;
import etec.src.sql.td.model.TableColumnModel;

/**
 * <h1>產製SDM</h1>
 * <p> 取出SQL中CREATE TABLE 語法
 * <br>並製作成SD文件
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
public class CreateSDIService {
	
	/**
	 * <h1>產製SD</h1>
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
	 * @throws IOException 
	 * */
	public static void createSDI(File f) throws IOException {
		String fileName = f.getName();
		String script = FileTool.readFile(f);
		//會出現create table 的情境有兩種
		//第一步要先清除註解
		//第二步要找出單純的Create multiset|set table語法，
		//最後要處理Create table as table
		script = cleanScript(script);
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
		String reg = "CREATE\\s+(?:(MULTISET|SET)\\s+)?TABLE\\s+([^\\s\\(]+)(?:\\s+[^\\(]+)?\\(([^;]+)\\)\\s*(?:PRIMARY\\s+INDEX\\s*\\(([^\\)]+)\\))?";
		Pattern p = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(script);
		while(m.find()) {
			String setType	 = m.group(1);//Set or Multiset
			String tableName = m.group(2);//target table name
			String context	 = m.group(3);//other script
			String primaryIndex = m.group(4);//primary index
			
			Map<String, TableColumnModel> colMap = TableColumnModel.convertToMap(context);
			for(String index : primaryIndex.split(",")) {
				colMap.get(index.trim().toUpperCase()).getSetting().setPrimaryIndex("Y");;
			}
			System.out.println();
		}
	}
	
	/**
	 * <h1>清理語句</h1>
	 * <p>為了方便之後的轉換，對語法進行初步的加工</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月13日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	script	原城市
	 * @throws	
	 * @see
	 * @return	
			 */
	private static String cleanScript(String script) {
		String res = script;
		/**
		 * <p>功能 ：清除註解</p>
		 * 	1.清除//跟--類型的註解
		 * 	2.將 *\/ 轉換成標記符號
		 *  3.刪除從/*到標記符號的內容
		 *	4.\r\n
		 * */
		res = res.replaceAll("(?://|--).*", "")
				.replaceAll("\\*/", Mark.MAHJONG_BLACK)
				.replaceAll("/*[^"+Mark.MAHJONG_BLACK+"]+"+Mark.MAHJONG_BLACK, "")
				.replaceAll("(?<!\r)\n","\r\n")
				;
		/**
		 * <p>功能 ：整理欄位設定</p>
		 * 	因為切欄位資訊時會以空白切分，要將依些設定合併
		 * 1.CHARACTER SET LATIN
		 * 2.NOT CASESPECIFIC
		 * 3.NOT NULL
		 * 4.TITLE 'AAA'
		 * 5.FORMAT 'AAA'
		 * 6.DEFAULT 'AAA'
		 * */
		res = res
				.replaceAll("(?i)\\bCHARACTER\\s+SET\\s+(\\S++)","CHARACTER_SET_$1")
				.replaceAll("(?i)\\bNOT\\s+(CASESPECIFIC|NULL)","NOT_$1")
				.replaceAll("(?i)\\b(TITLE|FORMAT)\\s+'([^']++)'","$1_'$2'")
				.replaceAll("(?i)\\bDEFAULT\\s+(\\S++)","DEFAULT_$1")
		;
		return res;
	}
	
}
