package src.java.service.transducer;

import java.io.IOException;
import java.util.List;

import src.java.tools.TransduceTool;

public class TransformTransducer {
	public static String run(String fn,String fc) throws IOException {
		String result = "Success";
		//語法轉換
		List<String> lstSql = TransduceTool.getRegexTarget("[Ss][Ee][Ll][Ee][Cc][Tt][^;]*;", fc);
		String content = "";
		for(String sql:lstSql) {
			String txt = TransduceTool.changeGroupBy(sql);
			txt = TransduceTool.arrangeSQL(txt);
			content += txt + "\r\n\r\n";
		}
		return result;
	}
	
}
