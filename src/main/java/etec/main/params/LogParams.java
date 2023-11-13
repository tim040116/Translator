package etec.main.params;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import etec.common.utils.ParamsTool;
import etec.main.ParamsFactory;

public class LogParams {

	// log的等級
	public final String PRINT_LEVEL;
	private final List<String> arrlv = new ArrayList<String>();
	public final boolean IS_WRITE_FILE;
	public final String LOG_FILE_NAME;
	// 時間的格式
	public SimpleDateFormat sf = new SimpleDateFormat("YYYY/MM/dd HH:mm:ss");

	public final String COLOR_DEBUG;
	public final String COLOR_INFO;
	public final String COLOR_WARN;
	public final String COLOR_ERROR;

	// 是否要加顏色
	public final boolean IS_COLOR;

	public LogParams(File f) {
		Map<String, String> map = ParamsTool.readParam("=", f);
		this.PRINT_LEVEL = map.get("PRINT_LEVEL").toUpperCase();
		this.IS_WRITE_FILE = map.get("IS_WRITE_FILE").toUpperCase().equals("TRUE");
		LOG_FILE_NAME = ParamsFactory.LOG_FILE_PATH+(new SimpleDateFormat("YYYY-MM-dd")).format(new Date())+".log";
		sf = new SimpleDateFormat(map.get("TIME_FORMAT"));
		this.IS_COLOR = "true".equals(map.get("IS_COLOR").toLowerCase());
		this.COLOR_DEBUG = map.get("COLOR_DEBUG");
		this.COLOR_ERROR = map.get("COLOR_ERROR");
		this.COLOR_INFO = map.get("COLOR_INFO");
		this.COLOR_WARN = map.get("COLOR_WARN");
		
		switch (PRINT_LEVEL) {
			case "DEBUG":
				arrlv.add("DEBUG");
			case "INFO":
				arrlv.add("INFO");
			case "WARN":
				arrlv.add("WARN");
			case "ERROR":
				arrlv.add("ERROR");
			default:
				break;
		}
		System.out.println();
	}
	
	public boolean levelContains(String str) {
		return arrlv.contains(str);
	}
	
}
