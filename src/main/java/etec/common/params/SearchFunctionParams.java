package etec.common.params;

import java.io.File;
import java.util.Map;

import etec.framework.file.params.service.ParamsTool;

public class SearchFunctionParams {

	public final String[] SKIP_LIST;

	public final String[] OVER_LIST;

	public final String DATA_TYPE_LIST;

	public final boolean  IS_TITLE = false;

	public final String FILE_LIST_NAME = "list\\file_list.csv";//列出所有檔案

	public final String FUNC_LIST_NAME = "list\\func_list.csv";//列出所有方法

	public final String DETL_LIST_NAME = "list\\detl_list.csv";//列出所有檔案用到的方法

	public final String DDL_LIST_NAME  = "list\\ddl_list.csv";//DDL用到的table

	public final String DQL_LIST_NAME  = "list\\dql_list.csv";//DQL用到的table

	public SearchFunctionParams(File f) {
		Map<String, String> map = ParamsTool.readParam("=",f);
		SKIP_LIST = map.get("SKIP_LIST").toUpperCase().split(",");
		OVER_LIST = map.get("OVER_LIST").toUpperCase().split(",");
		DATA_TYPE_LIST = map.get("DATA_TYPE_LIST").toUpperCase().replaceAll(",", "|");
	}
}
