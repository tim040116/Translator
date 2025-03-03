package etec.src.assignment.project.sd.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import etec.common.model.sql.CreateIndexModel;
import etec.common.model.sql.CreateTableModel;
import etec.common.model.sql.TableColumnModel;
import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.common.model.BasicParams;
import etec.src.translator.sql.td.TeradataSqlModelWrapper;

/**
 * 儲存建立SDI的功能
 *
 * */
public class CreateSDMService {


	/**
	 * @author	Tim
	 * @since	2023年9月15日
	 *
	 * SD檔案製作
	 * 只能處理CREATE TABLE語法
	 * 產生SDI.csv 紀錄每個資料表中各欄位的設定
	 *
	 * */
	public static String createSD(String fileName,String content) throws IOException {
		String result = "Success";
		String sdMainFileName = BasicParams.getOutputPath()+"list\\SDI_MAIN.csv";//列出所有檔案
		String sdDetailFileName = BasicParams.getOutputPath()+"list\\SDI_DETAIL.csv";//列出所有檔案

		File sdMainFile = new File(sdMainFileName);
		sdMainFile.getParentFile().mkdirs();
		if (!sdMainFile.exists()) {
			FileTool.addFile(sdMainFileName,"\"FILE_NAME\",\"DB_NAME\",\"TABLE_NAME\",\"SET_TABLE\",\"INDEX\"");//路徑,檔名,段落,方法名
        }

		File sdDetailFile = new File(sdDetailFileName);
		sdDetailFile.getParentFile().mkdirs();
		if (!sdDetailFile.exists()) {
			FileTool.addFile(sdDetailFileName ,"\"DB_NAME\",\"TABLE_NAME\",\"COLUMN_NAME\",\"COLUMN_TYPE\",\"CHARACTER\",\"CASESPECIFIC\",\"TITLE\",\"DEFAULT\",\"NOT_NULL\",\"OTHER\"");//路徑,檔名,類型,資料表名

		}

        //將Create table語法 轉成 CreateTableModel 物件
		TeradataSqlModelWrapper wp = new TeradataSqlModelWrapper();
		List<CreateTableModel> lstcm =  wp.createTable(content.replaceAll("\"Request Text\"",""));
		for(CreateTableModel cm : lstcm) {

			String dbNm = cm.getDatabaseName();
			String tableNm = cm.getTableName();
			String setTable = cm.getMultiSet();
			String index = "";
			for(CreateIndexModel m : cm.getIndex()) {
				index+=" "+m.toString();
			}
			for(TableColumnModel col : cm.getColumn()) {
				String colNm = col.getColumnName();
				String colType = col.getColumnType();
				String title = col.getSetting().getTitle();
				String notNull = col.getSetting().getNotNull();
				String casespecific = col.getSetting().getCasespecific();
				String format = col.getSetting().getFormat();
				String def = col.getSetting().getDefaultData();
				String character = col.getSetting().getCharacter();
				String other = col.getSetting().getOther();
				/*
				 * 202240408 Tim format 歸類在 Other
				 * */
				if(!"".equals(col.getSetting().getFormat())) {
					other +=  " FORMAT '" + col.getSetting().getFormat()+ "' ";
				}
				FileTool.addFile(sdDetailFileName,Charset.forName("BIG5"),
						  "\""+dbNm
						+ "\",\""+tableNm
						+ "\",\""+colNm
						+ "\",\""+colType
						+ "\",\""+character
						+ "\",\""+casespecific
						+ "\",\""+title
						+ "\",\""+def
						+ "\",\""+notNull
						+ "\",\""+other
						+ "\"");
			}
			FileTool.addFile(sdMainFileName,Charset.forName("BIG5"),
					  "\""   +fileName
				    + "\",\""+dbNm
					+ "\",\""+tableNm
					+ "\",\""+setTable
					+ "\",\""+index.trim()
					+ "\"");
		}

		return result;
	}


}
