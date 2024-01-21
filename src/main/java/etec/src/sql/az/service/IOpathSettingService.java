package etec.src.sql.az.service;

import etec.src.file.model.BasicParams;

public class IOpathSettingService {
	//讀黨產黨路徑設定
	public static void setPath(String input,String output){
		BasicParams.setInputPath(input);
		BasicParams.setOutputPath(output);
	}
}
