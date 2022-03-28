package src.java.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.java.model.BasicModel;

public class TransformTool {
	//取得符合正規表達式的字串
	public static List<String> getRegexTarget(String regex,String content) {
		List<String> lstRes = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);
		while (m.find()) {
			for(int i=0;i<=m.groupCount();i++) {
				lstRes.add(m.group(i));
			}
		}
		return lstRes;
	}
	public static String getTargetFileNm(String fileName) {
		String ip = BasicModel.getInputPath();
		String op = BasicModel.getOutputPath();
		return fileName.replace(ip, op);
	}
}
