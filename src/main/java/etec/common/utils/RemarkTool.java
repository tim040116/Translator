package etec.common.utils;

import java.util.HashMap;
import java.util.Map;

public class RemarkTool {

	// 區域
	private Map<String, String> mapText = new HashMap<String, String>();
	// 單行
	private Map<String, String> mapLine = new HashMap<String, String>();

	public String remark(String text) {
		StringBuffer res = new StringBuffer();
		String t = "";
		String old = "";
		String type = "";
		int cntText = 1;
		int cntLine = 1;
		for (String c : text.split("")) {
			// start
			if("".equals(type)) {
				if ("/*".equals(old + c)) {
					t="";
					type = "T";
				} 
				else if ("//".equals(old + c) || "--".equals(old + c)) {
					t="";
					type = "L";
				}
			}
			
			// end
			if("".equals(type)) {
				res.append(old);
				old=c;
			}
			else if ("T".equals(type)) {
				t+=old;
				old=c;
				if("*/".equals(old + c)) {
					String textId = "Remark_Line_"+cntText;
					mapText.put(textId, t+c);
					res.append("</"+textId+">");
					cntText++;
					t="";
					old="";
					type = "";
				}
			} 
			else if ("L".equals(type)) {
				t+=old;
				old=c;
				if("\n".equals(c)) {
					String lineId = "Remark_Line_"+cntLine;
					mapLine.put(lineId, t+c);
					res.append("</"+lineId+">");
					cntLine++;
					t="";
					old="";
					type = "";
				}
			}
		}
		return res.toString();
	}

}
