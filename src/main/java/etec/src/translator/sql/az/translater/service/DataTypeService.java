package etec.src.translator.sql.az.translater.service;

import java.util.ArrayList;
import java.util.List;

public class DataTypeService {

	private static String reg = "";
	static{
		List<String> lstType = new ArrayList<>();
		lstType.add("INTEGER");
		lstType.add("BIGINT");
		lstType.add("CHAR\\(\\d+\\)");
		lstType.add("VARCHAR\\(\\d+\\)");
		reg = "(?i)([.\\w]+(?:\\([^)]+\\))?)\\s*\\(\\s*("+String.join("|", lstType)+")\\s*\\)";
	}
	public static String changeStrongConvert(String sql) {
		String res = sql;
		res = res.replaceAll(reg,"CAST\\($1 AS $2\\)");
		return res;
	}

}
