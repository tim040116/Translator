package etec.sql.az.translater;

import java.io.IOException;
import java.util.List;

import etec.common.exception.UnknowSQLTypeException;
import etec.common.utils.Log;
import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;
import etec.sql.az.service.FamilyMartFileTransduceService;
import etec.src.main.Params;

/**
 * @author	Tim
 * @since	2023年11月1日
 * @version	3.3.1.1
 * 
 * 處理 SF SP 用到的不是單段語法
 * 
 * 
 * */
public class OtherTranslater {

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
				.replaceAll("(?i)\\bEXECUTE\\s+IMMEDIATE", "EXECUTE")
				.replaceAll("(?i)\\bFETCH\\b", "FETCH NEXT FROM")
				.replaceAll("(?i)\\bCLOSE\\s+([^\\s;]+)\\s*;", "CLOSE $1;\r\nDEALLOCATE $1;")
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
//		res = res
//				.replaceAll("(?i)END\\s+IF\\s*;","中")
//				.replaceAll("(?i)(\\s+)ELSE(\\s+)\\b([^中]+)\\b中", "$2END$1ELSE$2BEGIN$2$3$2END\r\n")
//				.replaceAll("中","END IF;")
//				;
		
		String tmp = res.replaceAll("\\b", TransduceTool.SPLIT_CHAR_RED);
		String tmp2 = "";
		int caseArea = 0;
		for(String str : tmp.split(TransduceTool.SPLIT_CHAR_RED)) {
			//計算 case when
			if("CASE".equals(str.toUpperCase())) {
				caseArea++;
			}
			if(caseArea>0&&"END".equals(str.toUpperCase())) {
					caseArea--;
			}
			//處理else
			if(caseArea<1&&"ELSE".equals(str.toUpperCase())) {
				str = "END ELSE BEGIN";
			}
			tmp2+=str;
		}
		res = tmp2;
		//if else if end if
		res = res
				.replaceAll("(?i)\\bEND\\s+IF\\s*;","END")
				.replaceAll("(?i)\\bIF\\s+(.*)\\sTHEN","IF $1 BEGIN")
				.replaceAll("(?i)\\bELSEIF\\s+(.*)THEN","END\r\nELSE IF $1 BEGIN")
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
					if(p.matches("\\s*")||p.matches("")) {
						continue;
					}
					t = t.replaceAll("\\b"+p.trim()+"\\b", "@"+p.trim());
				}
				return t;
			});
		}
		return res;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月13日
	 * 
	 * Call 語法轉成EXEC
	 * */
	public static String transduceCall(String script) {
		String res = script;
		res = res.replaceAll("(?i)CALL\\s+(\\S+)\\s*\\(([^\\)]+)\\)", "EXEC $1 $2");
		return res;
	}
	
	// index
	public static String changeIndex(String sql) {
		String result = sql;
		//取得sample
		List<String> lstIndex = RegexTool.getRegexTarget("(?<=[, ])[Ii][Nn][Dd][Ee][Xx][^\\)]+",result);
		//是否存在sample
		if(lstIndex.isEmpty()) {
			return sql;
		}
		for(String data : lstIndex) {
			String upper = data.toUpperCase();
			if(upper.contains("COLLECT STATISTICS ON")
					||upper.contains("PRIMARY")
					||upper.contains("UNIQUE")) {
				continue;
			}
			List<String>lstP = RegexTool.getRegexTarget("(?<=[Ii][Nn][Dd][Ee][Xx]\\s{0,10}\\()[^\\)]+",data);
			if(lstP.isEmpty()) {
				continue;
			}
			String params = lstP.get(0);
			String[] arp = params.split(",");
			if(arp.length!=2) {
				continue;
			}
			String index = " CHARINDEX("+arp[1]+","+arp[0];
			String reg = RegexTool.encodeSQL(data);
			result = RegexTool.encodeSQL(result).replaceAll(reg,RegexTool.encodeSQL(index));
		}
		result = RegexTool.decodeSQL(result);
		return result;
	}
	
	
}
