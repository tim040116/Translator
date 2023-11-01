package etec.main.params;

import java.io.File;
import java.util.Map;

import etec.common.utils.ParamsTool;

public class ConfigParams {
	
	public final String APPLICATION_TYPE;
	
	public final String INIT_INPUT_PATH;
	
	public final String INIT_OUTPUT_PATH;
	
	public ConfigParams(File f) {
		Map<String, String> map = ParamsTool.readParam("=",f);
		APPLICATION_TYPE = map.get("APPLICATION_TYPE");
		INIT_INPUT_PATH  = map.get("INIT_INPUT_PATH");
		INIT_OUTPUT_PATH = map.get("INIT_OUTPUT_PATH");
	}
}
