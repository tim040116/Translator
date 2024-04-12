package etec.src.sql.azure.list;

import java.io.IOException;

import etec.common.model.sql.CreateIndexModel;
import etec.common.model.sql.CreateTableModel;
import etec.common.utils.file.FileTool;
import etec.common.model.sql.CreateTableModel;

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
	
	public static String CreateList(String script) throws IOException {
		String result = "Success";
		
		//設定檔案路徑名稱與表頭
		String sdMainFileName = BasicParams.getOutputPath()+"list\\MultisetList.csv";//列出所有檔案
		FileTool.addFile(sdMainFileName,"\"DB_NAME\",\"TABLE_NAME\",\"SET_TABLE\",\"INDEX\"");//路徑,檔名,段落,方法名

		TeradataSqlModelWrapper wp = new TeradataSqlModelWrapper();
		CreateTableModel cm = wp.createTable(script.replaceAll("\"Request Text\"",""));
		String dbNm = cm.getDatabaseName();
		String tableNm = cm.getTableName();
		String setTable = cm.getMultiSet();
		String index = "";
		for(CreateIndexModel m : cm.getIndex()) {
			index+=" "+m.toString();
		}
		if("MULTISET".equals(setTable) ) {
			FileTool.addFile(sdMainFileName,																
					  "\""+dbNm
					+ "\",\""+tableNm
					+ "\",\""+setTable
					+ "\",\""+index.trim()
					+ "\"");
		}
		
		return result;		
		
	}
}
