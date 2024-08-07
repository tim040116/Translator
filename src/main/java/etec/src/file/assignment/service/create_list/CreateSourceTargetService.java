package etec.src.file.assignment.service.create_list;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.Mark;

public class CreateSourceTargetService {

	public void importFile(String fileName,String content) {
		content = cleanScript(content); 
		//找語法
		Matcher m = Pattern.compile("(?i)\\b(CREATE|INSERT|RENAME|MERGE)\\b[^;]+;").matcher(content);
		while(m.find()) {
			switch (m.group(1).toUpperCase()) {
			case "":
				
				break;

			default:
				break;
			}
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

class SourceTargetModel{
	String fileName;
	String sourceTable;
	String targetTable;
	MappingType type;
}

enum MappingType{
	CTAS,
	INSERT,
	JOIN,
	UPDATE,
	RENAME
}
