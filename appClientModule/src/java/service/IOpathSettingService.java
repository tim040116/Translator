package src.java.service;

import src.java.model.BasicModel;

public class IOpathSettingService {
	//讀黨產黨路徑設定
	public static void setPath(String input,String output){
		BasicModel.setInputPath(input);
		BasicModel.setOutputPath(output);
	}
}
