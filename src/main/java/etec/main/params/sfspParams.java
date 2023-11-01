package etec.main.params;

import java.io.File;
import java.util.Map;

import etec.common.utils.ParamsTool;

public class sfspParams {
	
	public final boolean TRANS_PARAMS;
	public final boolean TRANS_IF_ELSE;
	
	public sfspParams(File f) {
		Map<String, String> map = ParamsTool.readParam("=",f);
		this.TRANS_PARAMS = map.get("TRANS_PARAMS").toUpperCase().equals("TRUE");
		this.TRANS_IF_ELSE = map.get("TRANS_IF_ELSE").toUpperCase().equals("TRUE");
	}
}
