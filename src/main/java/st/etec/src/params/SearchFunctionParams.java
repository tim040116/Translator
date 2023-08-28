package st.etec.src.params;

import java.io.File;
import java.util.Map;

public class SearchFunctionParams {
	
	public final String[] SKIP_LIST;
	
	public SearchFunctionParams(File f) {
		Map<String, String> map = ParamsService.readParam("=",f);
		SKIP_LIST = map.get("SKIP_LIST").toUpperCase().split(",");
	}
}
