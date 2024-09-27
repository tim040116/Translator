package etec.common.params;

import java.io.File;
import java.util.Map;

import etec.framework.file.params.service.ParamsTool;

public class ConfigParams {

	public final String APPLICATION_TYPE;

	public final String INIT_INPUT_PATH;

	public final String INIT_OUTPUT_PATH;

	public final boolean BIG_FILE_SPLIT;

	public ConfigParams(File f) {
		Map<String, String> map = ParamsTool.readParam("=",f);
		APPLICATION_TYPE = map.get("APPLICATION_TYPE");
		INIT_INPUT_PATH  = map.get("INIT_INPUT_PATH");
		INIT_OUTPUT_PATH = map.get("INIT_OUTPUT_PATH");
		BIG_FILE_SPLIT = map.get("BIG_FILE_SPLIT").matches("(?i)YES|Y|T|TRUE");
	}
}
