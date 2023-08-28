 package st.etec.src.params;

import java.io.File;
import java.util.Map;

/**
 * 2023/08/01 Tim
 * 將一些參數改成讀取參數檔
 * 
 * 
 * 
 * */
public class ParamsFactory {
	
	//設定檔路徑
//	public static final String ROOT_CONFIG_PATH = "C:\\Users\\User\\Desktop\\Trans\\search_function\\config\\";
	public static final String ROOT_CONFIG_PATH = "config\\";

	//要讀取的設定檔
	public static final String[] CONFIG_FILE_LIST  = {
		"config",
		"log",
		"search_function"
	};
	
	private static Map<String,File> mapParams;
	
	public static void init() {
		mapParams = ParamsService.readConfigFile();
		Params.log = new LogParams(mapParams.get("log"));
		Params.config = new ConfigParams(mapParams.get("config"));
		Params.searchFunction = new SearchFunctionParams(mapParams.get("search_function"));
	}
}
