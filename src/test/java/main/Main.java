package main;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.file.CharsetTool;
import etec.src.sql.az.translater.DDLTranslater;
import etec.src.sql.az.translater.DMLTranslater;

/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";
	
	public static void main(String[] args) {
		try {
			String res = test();
			Pattern p = Pattern.compile("(?mi)(?:^\\s*\\#.*)|--.*|\\/\\*[\\S\\s]+?\\*\\/");
			Matcher m = p.matcher(res);
			while (m.find()) {
				System.out.println(m.group());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String test() {
		return "Insert into ${TEMP}.TP1_AGMCLASS\r\n" + 
				"(\r\n" + 
				"       \"CLASS_ID\"                             	  -- VARCHAR(2)<-VARCHAR(2)                                       AGMCLASS.\"CLASS_ID\"\r\n" + 
				"      ,\"CLASS_NAME\"                            	  -- VARCHAR(20) <- VARCHAR(20)                                   AGMCLASS.\"CLASS_NAME\"\r\n" + 
				"      ,\"UPDATE_TIME\"                           	  -- TIMESTAMP(0)<-TIMESTAMP(0)                                   AGMCLASS.\"UPDATE_TIME\"\r\n" + 
				"      ,\"USE_KIND_NO\"                          	  -- VARCHAR(2)<-VARCHAR(20)                                      AGMCLASS.\"USE_KIND_NO\"\r\n" + 
				")\r\n" + 
				"Select\r\n" + 
				"       Trim(Coalesce(\"CLASS_ID\",'')) AS \"CLASS_ID\"             -- (\"FM_CODE\")  VARCHAR(7)-> VARCHAR(7)    --[\"FM_CODE\"]\r\n" + 
				"      ,Trim(\"CLASS_NAME\") AS \"CLASS_NAME\"                      -- (\"CLASS_NAME\")  VARCHAR(20)-> VARCHAR(20)    --[\"CLASS_NAME\"]\r\n" + 
				"      ,\"UPDATE_TIME\" AS \"UPDATE_TIME\"                          -- (\"UPDATE_TIME\")  TIMESTAMP(0)-> TIMESTAMP(0)    --[\"UPDATE_TIME\"]\r\n" + 
				"      ,Trim(\"USE_KIND_NO\") AS \"USE_KIND_NO\"                    -- (\"USE_KIND_NO\")  VARCHAR(20)-> VARCHAR(20)    --[\"USE_KIND_NO\"]\r\n" + 
				" From ${STAGE}.AGMCLASS\r\n" + 
				";";
	}
	
}
