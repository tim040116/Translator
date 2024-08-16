package etec.common.factory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import etec.common.params.ConfigParams;
import etec.common.params.FamilyMartParams;
import etec.common.params.GreenPlumParams;
import etec.common.params.LogParams;
import etec.common.params.SFSPParams;
import etec.common.params.SearchFunctionParams;
import etec.framework.file.params.service.ParamsTool;

/**
 * <h1>外部參數</h1>
 * 
 * <p>預計在未來改成Map形式
 * <p>預計將ROOT_PATH放入resource
 * 
 * <br>2024/01/21 Tim	將Params跟ParamFactory合併，執行時不需先init
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	dev
 * 
 * */
public class Params {
	
	//設定檔路徑
//	public static final String ROOT_PATH = "C:\\Users\\User\\Desktop\\Trans\\";
	public static final String ROOT_PATH = "";
	
	public static final String[] CONFIG_FILE_LIST = {
			"config"
			,"log"
			,"familymart" 
			,"search_function"
			,"sf_sp"
			,"greenplum"
	};
	
	//所有的參數檔
	private static Map<String,File> mapParams;
	
	public static ConfigParams config = null;
	
	public static LogParams log = null;
	
	public static SearchFunctionParams searchFunction = null;
	
	public static FamilyMartParams familyMart = null;

	public static SFSPParams sfsp = null;
	
	public static GreenPlumParams gp = null;
	
	static{
		try {
			mapParams = ParamsTool.readConfigFile(ROOT_PATH+"config\\",".txt");
			Params.log = new LogParams(mapParams.get("log"));
			Params.config = new ConfigParams(mapParams.get("config"));
			Params.searchFunction = new SearchFunctionParams(mapParams.get("search_function"));
			Params.familyMart = new FamilyMartParams(mapParams.get("familymart"));
			Params.sfsp = new SFSPParams(mapParams.get("sf_sp"));
			Params.gp = new GreenPlumParams(mapParams.get("greenplum"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}