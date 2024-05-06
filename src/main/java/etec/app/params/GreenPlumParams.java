package etec.app.params;

import java.io.File;
import java.util.Map;

import etec.common.utils.param.ParamsTool;

/**
 * @author	Tim
 * @since	2023年12月21日
 * @version	4.0.0.0
 * 
 * GreenPlum轉換工具的參數
 * 
 * */
public class GreenPlumParams {
	
	/**
	 * @author	Tim
	 * @since	2023年12月21日
	 * DROP TABLE及DROP VIEW是否要加CASCADE
	 * */
	public final boolean IS_CASCADE;
	
	public final String CHARSET;
	
	public GreenPlumParams(File f) {
		Map<String, String> map = ParamsTool.readParam("=",f);
		this.IS_CASCADE = map.get("IS_CASCADE").toUpperCase().equals("TRUE");
		this.CHARSET = map.get("CHARSET").toUpperCase();
	}
}
