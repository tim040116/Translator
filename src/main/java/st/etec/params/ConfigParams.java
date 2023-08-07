package st.etec.params;

import java.io.File;
import java.util.Map;

public class ConfigParams {
	
	public final String INIT_INPUT_PATH;
	
	public final String INIT_OUTPUT_PATH;
	
	public ConfigParams(File f) {
		Map<String, String> map = ParamsService.readParam("=",f);
		INIT_INPUT_PATH  = map.get("INIT_INPUT_PATH");
		INIT_OUTPUT_PATH = map.get("INIT_OUTPUT_PATH");
	}
}
