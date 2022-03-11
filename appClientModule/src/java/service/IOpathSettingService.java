package src.java.service;

import src.java.model.IOPathModel;

public class IOpathSettingService {
	//讀黨產黨路徑設定
	public static void setPath(String input,String output){
		IOPathModel.setInputPath(input);
		IOPathModel.setOutputPath(output);
	}
}
