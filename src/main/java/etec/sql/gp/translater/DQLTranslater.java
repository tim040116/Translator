package etec.sql.gp.translater;

import etec.common.utils.TransduceTool;

public class DQLTranslater {
	

	public String easyReplase(String script) {
		String res = script
				;
		res = TransduceTool.saveTranslateFunction(res, (String t)->{
			return t;
		});
		return res;
	}
	

}
