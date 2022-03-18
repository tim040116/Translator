package src.java.service;

import src.java.model.BasicModel;

public class IOpathSettingService {
	//讀黨產黨路徑設定
	public static void setPath(String input,String output){
		BasicModel.setInputPath(fix(input));
		BasicModel.setOutputPath(fix(output));
	}
	private static String fix(String s) {
		String r = s;
		//
		if(!"\\".equals(s.substring(s.length() - 1))) {
			r=r+"\\";
		}
		return r;
	}
}
