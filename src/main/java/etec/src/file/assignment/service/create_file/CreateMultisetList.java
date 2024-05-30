package etec.src.file.assignment.service.create_file;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.model.sql.CreateIndexModel;
import etec.common.model.sql.CreateTableModel;
import etec.common.utils.file.FileTool;
import etec.common.utils.param.Params;
import etec.src.file.model.BasicParams;
import etec.src.sql.az.wrapper.TeradataSqlModelWrapper;

/**
 * 
 * 
 * @author	Tim
 * @since	4.0.0.0
 * @version	4.0.0.0
 * 
 * */
public class CreateMultisetList {
	
	private static boolean isFileExist = false;
	
	private static String sdMainFileName = "";
	
	public static String CreateList(String rootPath, File f) throws IOException {
		String result = "Success";
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
