package etec.src.assignment.project.sd.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.common.model.BasicParams;

/**
 * 
 * 
 * @author	Tim
 * @since	4.0.0.0
 * @version	4.0.0.0
 * 
 * */
public class CreateMultisetListService {
	
	private static boolean isFileExist = false;
	
	private static String sdMainFileName = "";
	
	public static String CreateList(String rootPath, File f) throws IOException {
		String result = "Success";
		isFileExist = false;
		//設定檔案路徑名稱與表頭
		if(!isFileExist) {
			sdMainFileName = BasicParams.getOutputPath()+"list\\MultisetList.csv";//列出所有檔案
			FileTool.addFile(sdMainFileName,"\"PATH_NAME\",\"FILE_NAME\",\"SCHEMA_NAME\",\"TABLE_NAME\"");//路徑,檔名,段落,方法名
			isFileExist = true;
		}
		
		//讀檔
		String content = FileTool.readFile(f);
		String FileName = f.getName();
		String pathName = f.getPath()
				.replace(rootPath,"")
				.replace(FileName, "")
		;
		String reg = "(?i)\\bCREATE\\s+MULTISET\\s+(?:VOLATIE\\s+)?TABLE\\s+(?:([^.]+)\\.)?(\\S+)";
		Matcher m = Pattern.compile(reg).matcher(content);
		while(m.find()) {
			String schemaName = m.group(1);
			String tableName  = m.group(2);
			FileTool.addFile(sdMainFileName,																
					  "\""+pathName
					+ "\",\""+FileName
					+ "\",\""+schemaName
					+ "\",\""+tableName
					+ "\"");
		}
		
		return result;		
		
	}
}
