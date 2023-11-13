package etec.src.transducer;

import java.util.List;

import etec.common.utils.RegexTool;
import etec.main.Params;

/**
 * @author	Tim
 * @since	2023年11月1日
 * @version	3.3.1.1
 * 
 * 處理 SF SP 用到的不是單段語法
 * 
 * 
 * */
public class SFTransducer {

	/**
	 * @author	Tim
	 * @since	2023年10月27日
	 * 
	 * -   轉換Cursor跟 label語法
	 * 1.EXECUTE  後面的字拿掉
		ex: EXECUTE IMMEDIATE -> EXECUTE
		2.FETCH 改成 FETCH NEXT FROM
		3.CLOSE (\S) 改成 CLOSE $1 \r\nDEALLOCATE $1
	 * */
	public static String transduceCursor(String script) {
		String txt = script.toUpperCase();
		txt = txt
				.replaceAll("\\bEXECUTE\\s+IMMEDIATE", "EXECUTE")
				.replaceAll("\\bFETCH\\b", "FETCH NEXT FROM")
				.replaceAll("\\bCLOSE\\s+([^\\s;]+)\\s*;", "CLOSE $1;\r\nDEALLOCATE $1;")
				;
		/**
		 * 2023-11-06 Tim : 經測試，Cursor語法僅支援MS SQL 不支援Azure，因此廢棄
		 * 
		 * */
//		txt = txt
//				.replaceAll("PREPARE\\s(\\S+)\\sFROM\\s+(\\S+)\\s*;"
//						, "set @SqlCur = N'DECLARE $1 CURSOR FOR ' + $2 ;\r\n\tEXECUTE sp_executesql @SqlCur")
//				;
		return txt;
	}
	
	/**
	 * @author	Tim
	 * @since	2023年11月1日
	 * IF ELSEIF ELSE的語法轉換
	 * 
	 * */
	public static String transduceIF(String script) {
		String res = script.toUpperCase();
		//先處理else 因為會跟case的搞混
		res = res
				.replaceAll("END\\s+IF\\s*;","中")
				.replaceAll("(\\s+)ELSE(\\s+)\\b([^中]+)\\b中", "$2END$1ELSE$2BEGIN$2$3$2END\r\n")
				.replaceAll("中","END IF;")
				;
		//if else if end if
		res = res
				.replaceAll("\\bEND\\s+IF\\s*;","END")
				.replaceAll("\\bIF\\s+(.*)\\sTHEN","IF $1 BEGIN")
				.replaceAll("\\bELSEIF\\s+(.*)THEN","END\r\nELSE IF $1 BEGIN")
				;
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月1日
	 * 參數轉換
	 * */
	public static String transduceDECLARE(List<String> lstParams,String script) {
		String res = script;
		//參數置換
		if(Params.sfsp.TRANS_PARAMS) {
			//置換
			res = RegexTool.spaceRun(res, (String t) -> {
				for(String p: lstParams) {
					if(p.matches("\\s*")) {
						continue;
					}
					t = t.replaceAll("\\b"+p.trim()+"\\b", "@"+p.trim());
				}
				return t;
			});
		}
		return res;
	}
}
