package etec.common.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import etec.framework.file.params.service.ResourceTool;

/**
 * 從版本紀錄取得版本資訊
 * */
public class VersionModel {

	public static final String ALL_LOG;

	public static final String VERSION;

	public static final Date VERSION_DATE;

	public static final String LAST_UPDATE_MEMBER;

	public static final String VERSION_NAME;

	static{
		String version = "5.1.4.0";
		Date vsDate = new Date("2024/09/30");
		String member = "王小明";
		String versionName = "Hello World";
		String allLog="";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/mm/dd",Locale.TAIWAN);

//		InputStream in = Main.class.getClassLoader().getResourceAsStream("/版本紀錄");
		
		try {
			ResourceTool rt = new ResourceTool();
			String str = rt.readFile("doc/版本紀錄");
			String[] arr = null;
			arr = str.trim().split("\\s+", 5);
			version = arr[0];
			vsDate = sf.parse(arr[1]);
			member = arr[2];
			versionName = arr[3];
		} catch (ParseException e) {
			e.printStackTrace();
		}

		VERSION = version;
		VERSION_DATE = vsDate;
		LAST_UPDATE_MEMBER = member;
		VERSION_NAME = versionName;
		ALL_LOG = allLog;
	}
}
