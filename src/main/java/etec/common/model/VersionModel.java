package etec.common.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import etec.framework.file.readfile.service.FileTool;

/**
 * 從版本紀錄取得版本資訊
 * */
public class VersionModel {

	public static final String VERSION;
	
	public static final Date VERSION_DATE;
	
	public static final String LAST_UPDATE_MEMBER;

	public static final String VERSION_NAME;
	
	static{
		String version = "0";
		Date vsDate = new Date();
		String member = "王小明";
		String versionName = "Hello World";
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/mm/dd",Locale.TAIWAN);
		try (
				FileInputStream fis = new FileInputStream(new File("doc\\版本紀錄"));
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(isr);
		){
			String[] arr = null;
			while(br.ready()) {
				String line = br.readLine();
				if(line.matches("\\s*")){continue;}
				arr = line.trim().split("\\s+", 4);
				break;
			}
			version = arr[0];
			vsDate = sf.parse(arr[1]);
			member = arr[2];
			versionName = arr[3];
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		VERSION = version;
		VERSION_DATE = vsDate;
		LAST_UPDATE_MEMBER = member;
		VERSION_NAME = versionName;
	}
}
