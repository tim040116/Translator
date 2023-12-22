 package etec.src.main;

import java.io.File;
import java.util.Map;

import etec.common.utils.ParamsTool;
import etec.src.params.ConfigParams;
import etec.src.params.FamilyMartParams;
import etec.src.params.GreenPlumParams;
import etec.src.params.LogParams;
import etec.src.params.SearchFunctionParams;
import etec.src.params.SFSPParams;

/**
 * 2023/08/01 Tim
 * 將一些參數改成讀取參數檔
 * 
 * 
 * 
 * */
public class ParamsFactory {
	
	//設定檔路徑
	public static final String ROOT_CONFIG_PATH = "C:\\Users\\User\\Desktop\\Trans\\config\\";
//	public static final String ROOT_CONFIG_PATH = "config\\";
	
	//設定Log路徑
	public static final String LOG_FILE_PATH = "C:\\Users\\User\\Desktop\\Trans\\log\\";
//	public static final String LOG_FILE_PATH = "log\\";
	
	//要讀取的設定檔
	public static final String[] CONFIG_FILE_LIST  = {
		"config",
		"log",
		"familymart",
		"search_function",
		"sf_sp",
		"greenplum"
	};
	
	private static Map<String,File> mapParams;
	
	public static void init() {
		mapParams = ParamsTool.readConfigFile();
		Params.log = new LogParams(mapParams.get("log"));
		Params.config = new ConfigParams(mapParams.get("config"));
		Params.searchFunction = new SearchFunctionParams(mapParams.get("search_function"));
		Params.familyMart = new FamilyMartParams(mapParams.get("familymart"));
		Params.sfsp = new SFSPParams(mapParams.get("sf_sp"));
		Params.gp = new GreenPlumParams(mapParams.get("greenplum"));
	}
}
