package etec.src.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import etec.common.model.BasicParams;
import etec.common.model.sql.CreateIndexModel;
import etec.common.model.sql.CreateTableModel;
import etec.common.model.sql.TableColumnModel;
import etec.common.utils.FileTool;
import etec.common.utils.Log;
import etec.common.utils.RegexTool;
import etec.main.Params;
import etec.sql.wrapper.impl.TeradataSqlModelWrapper;

/**
 * 儲存所有建立清單的功能
 * 
 * */
public class CreateListService {
	
	/**
	 * @author	Tim
	 * @throws IOException 
	 * @since	2023年9月21日
	 * 
	 * 無論如何先產一份檔案清單檔
	 * */
	public static void createFileList(File f) throws IOException {
		
		String fileListNm = BasicParams.getOutputPath()+Params.searchFunction.FILE_LIST_NAME;//列出所有檔案
		String path = "\\" + f.getPath()
		.replace(BasicParams.getInputPath(), "")
		.replace(f.getName(), "")
		.replaceAll("\\\\$", "")
		;
		String[] arrFileType = f.getName().split("\\.");
		String fileType = arrFileType[arrFileType.length-1];
		FileTool.addFile(fileListNm,																
				  "\""   +path 
				+ "\",\""+f.getName()
				+ "\",\""+fileType
				+ "\",\""+f.length()
				+ "\"");
	}
	
	/**
	 * @author	Tim
	 * @since	2023年9月15日
	 * 
	 * SD檔案製作
	 * 只能處理純CREATE TABLE語法
	 * 產生兩個檔案
	 * 	1.Table_list.csv 紀錄每個資料表的設定
	 * 	2.SDI.csv 紀錄每個資料表中各欄位的設定
	 * 
	 * */
	public static String createSD(String content) throws IOException {
		String result = "Success";
		String sdMainFileName = BasicParams.getOutputPath()+"list\\SD_MAIN.csv";//列出所有檔案
		String sdDetailFileName = BasicParams.getOutputPath()+"list\\SD_DETAIL.csv";//列出所有檔案
		FileTool.addFile(sdMainFileName,"\"DB_NAME\",\"TABLE_NAME\",\"SET_TABLE\",\"INDEX\"");//路徑,檔名,段落,方法名
		FileTool.addFile(sdDetailFileName ,"\"DB_NAME\",\"TABLE_NAME\",\"COLUMN_NAME\",\"COLUMN_TYPE\",\"CHARACTER\",\"CASESPECIFIC\",\"TITLE\",\"DEFAULT\",\"NOT_NULL\",\"OTHER\"");//路徑,檔名,類型,資料表名
		TeradataSqlModelWrapper wp = new TeradataSqlModelWrapper();
		CreateTableModel cm = wp.createTable(content.replaceAll("\"Request Text\"",""));
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
			FileTool.addFile(sdDetailFileName,																
					  "\""+dbNm
					+ "\",\""+tableNm
					+ "\",\""+colNm
					+ "\",\""+colType
					+ "\",\""+character
					+ "\",\""+casespecific+" "+format
					+ "\",\""+title
					+ "\",\""+def
					+ "\",\""+notNull
					+ "\",\""+other
					+ "\"");
		}
		FileTool.addFile(sdMainFileName,																
				  "\""+dbNm
				+ "\",\""+tableNm
				+ "\",\""+setTable
				+ "\",\""+index.trim()
				+ "\"");
		return result;
	}
	
	//SD
		public static String column(String column){
			String result = "";
			column = column
					.replaceAll("\\s*(\\s*", "(")
					.replaceAll("\\s*)", "(")
					.replaceAll("\\s*,\\s*", ",")
					;
			return result;
		}
	
	// create table
	public static String createCreateTable(String fn, String fc) {
		String result = "Success";
//		String lstFileName = BasicParams.getOutputPath() + "lst\\lst_create_table.txt";
		String lstFileName2 = BasicParams.getOutputPath() + "lst\\lst_create_table_withfilenm.txt";

		// 尋找create table
		List<String> lstCreate = RegexTool
				.getRegexTarget("(?<=[Cc][Rr][Ee][Aa][Tt][Ee]\\s{0,10}[Tt][Aa][Bb][Ll][Ee]\\s{0,10})\\S+", fc);
		for (String sql : lstCreate) {
			try {
//				ReadFileTool.addFile(lstFileName,sql);
				FileTool.addFile(lstFileName2,fn+"\t"+sql);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	// FAST LOAD
	public static String createFastloadLst(String fn, String fc) throws IOException {
		Log.info("fastload");
		String result = "";
		String file = BasicParams.getOutputPath() + "lst\\lst_fastload.txt";
		String txt = "";
		boolean flag = false;
		for (String line : fc.split("\r\n")) {
			line = line.replaceAll("\\s+", " ");
			if (line.contains("SET RECORD")) {
				flag = true;
				String rec = line;
				if (!line.contains("UNFORMATTED")) {
					rec = rec.replace("VARTEXT", "").replace("DISPLAY_ERRORS NOSTOP", "");
				}
				rec = rec.replace("SET RECORD", "").replace(" ", "").replace(";", "");
				rec = rec.trim();
				if (!line.contains("UNFORMATTED")) {
					rec = rec.substring(1, rec.length()-1);
				}
				txt = fn + "\t" + rec;
			}
			if (flag) {
				if (line.contains("INSERT INTO")) {
					String ins = line.replace("INSERT INTO", "").replace("(", "");
					txt = txt + "\t" + ins;
					if (txt.contains("UNFORMATTED")) {
						FileTool.addFile(file, txt);
					}
				}
			}
		}
		return result;
	}
	// 產檔export_lst
	public static String createExportLst(String fn, String fc) throws IOException {
		Log.info("buildExportListFile");
		String result = "Success";
		String file = BasicParams.getOutputPath() + "lst\\lst_export.txt";
		List<String> lstTrg = RegexTool.getRegexTarget("my \\$OUTPUT_FILE[^;]*;", fc);
		if (!lstTrg.isEmpty()) {
			String target = lstTrg.get(0).replaceAll("my|\\s|\\$OUTPUT_FILE|=|\"|;", "");
			FileTool.addFile(file, fn + "\t" + target);
		}
		return result;
	}
	//With data
	public static String createWithData(String fn, String fc) {
		String result = "Success";
		String lstFileName = BasicParams.getOutputPath() + "lst\\lst_CTE.txt";
		String root = BasicParams.getInputPath().replaceAll("\\\\", "<encodingCode_BackSlash>");
		String tfn = fn.replaceAll("\\\\", "<encodingCode_BackSlash>");
		String fn2 = tfn.replaceAll(root,"");
		fn2 = fn2.replaceAll("<encodingCode_BackSlash>","\\\\");
		//修改分號
		String content = fc.replaceAll(";(?!\\s)","<encodingCode_semicolon>");
		//尋找create table
		List<String> lstCreate = RegexTool.getRegexTarget(RegexTool.getReg("Create Table [^;]+;"),content);
		for (String sql : lstCreate) {
			if(!RegexTool.getRegexTarget(RegexTool.getReg("With Data"),sql).isEmpty()) {
				String tableNm = RegexTool.getRegexTarget("(?<=[Cc][Rr][Ee][Aa][Tt][Ee] {0,10}[Tt][Aa][Bb][Ll][Ee] {0,10})\\S+",sql).get(0);
				try {
					FileTool.addFile(lstFileName, fn2 + "\t" + tableNm);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	//產檔index
	public static String createIndexLst(String tblNm, String target, String fn) throws IOException {
		Log.info("buildIndexListFile");
		String result = "Success";
		if(!target.matches(".*[Ii][Nn][Dd][Ee][Xx].*")) {
			return result;
		}
		String file = BasicParams.getOutputPath() + "lst\\lst_index.txt";
		FileTool.addFile(file,fn + "\t" + tblNm + "\t" + target);
		return result;
	}
	//產檔create select
	public static String createCreateSelectLst(String fn,String data) throws IOException {
		Log.info("buildCreateSelectListFile");
		String result = "Success";
		String file = BasicParams.getOutputPath() + "lst\\lst_create_select.txt";
		String create = "";
		String select = "";
		List<String> lstcreate = RegexTool.getRegexTarget("(?<=[Cc][Rr][Ee][Aa][Tt][Ee]\\s{0,10}[Tt][Aa][Bb][Ll][Ee])\\s+\\S*",data);
		if(!lstcreate.isEmpty()) {
			create = lstcreate.get(0).trim();
		}
		List<String> lstselect = RegexTool.getRegexTarget("(?<=[Ff][Rr][Oo][Mm])\\s+\\S+",data);
		if(!lstselect.isEmpty()) {
			select = lstselect.get(0).trim();
		}
		FileTool.addFile(file,fn+"\tcreate "+create+"\tselect "+select);
		return result;
	}
	//Qualify Rank
	public static String createQualifyRank(String fn, String fc) {
		String result = "Success";
		String lstFileName = BasicParams.getOutputPath() + "lst\\lst_Qualify.txt";
		List<String> lstQualify = RegexTool.getRegexTarget(
				"[Qq][Uu][Aa][Ll][Ii][Ff][Yy] +[Rr][Aa][Nn][Kk]\\(\\) [Oo][Vv][Ee][Rr] \\([^\\)]*\\) *\\= *\\d+",
				fc);
		for (String qualify : lstQualify) {
			try {
				FileTool.addFile(lstFileName, fn + "\t" + qualify);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	//ODBC
	public static String createODBC(String fn, String fc) {
		String result = "Success";
		String lstFileName = BasicParams.getOutputPath() + "lst\\lst_odbc.txt";
		List<String> lstODBC = RegexTool
				.getRegexTarget("(?<=[Mm][Yy] {0,10}\\$[Dd][Ss][Nn] {0,10}= {0,10}\\\")[^\\\"]+", fc);
		for (String qualify : lstODBC) {
			try {
				FileTool.addFile(lstFileName, fn + "\t" + qualify);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	//group by
	public static String createGroupBy(String fn, String fc) {
		String result = "Success";
		String lstFileName = BasicParams.getOutputPath() + "lst\\lst_GroupBy.txt";
		List<String> lstGroupby = RegexTool.getRegexTarget("[Gg][Rr][Oo][Uu][Pp] +[Bb][Yy][ 0-9,]+", fc);
		for (String data : lstGroupby) {
			if(data.matches("[^0-9]*")) {
				continue;
			}
			try {
				FileTool.addFile(lstFileName, fn + "\t" + data.replaceAll("[\r\n]", " "));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	//cast as Char10
	public static String createChar10(String fn, String fc) {
		String result = "Success";
		String lstFileName = BasicParams.getOutputPath() + "lst\\lst_char10.txt";
		List<String> lstChar10 = RegexTool.getRegexTarget2("[Cc][Aa][Ss][Tt] *\\( *[\\w\\.]+ +[Aa][Ss] +([Vv][Aa][Rr])?[Cc][Hh][Aa][Rr] *\\( *10 *\\) *\\)", fc);
		for (String data : lstChar10) {
			try {
				FileTool.addFile(lstFileName, fn + "\t" + data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	// 產檔lst Rename Table 
	public static String createLstRenameTable(String fn, String fc) throws IOException {
		Log.info("createLstRenameTable");
		String result = "Success";
		String file = BasicParams.getOutputPath() + "lst\\lst_rename_table.txt";
//		String file2 = BasicParams.getOutputPath() + "lst\\lst_rename_table_input.txt";
		String strAll = fc.replaceAll(";","").replaceAll(RegexTool.getReg("rename table"), "");
		String[] arAll = strAll.split(" +[Tt][Oo] +");
		String[] arOld = arAll[0].trim().split("\\.");
		String[] arNew = arAll[1].trim().split("\\.");
//		FileTool.addFile(file2,fn+"\t"+arOld[0]+"\t"+arOld[1]+"\t"+arNew[0]+"\t"+arNew[1]);
		FileTool.addFile(file,fn+"\t"+arOld[0]+"."+arOld[1]+"\tto\t"+arNew[0]+"."+arNew[1]);
		return result;
	}
	// 產檔lst drop Table 
	public static String createLstDropTable(String fn, String fc) throws IOException {
		Log.info("createLstDropTable");
		String result = "Success";
		String file = BasicParams.getOutputPath() + "lst\\lst_drop_table.txt";
		String strAll = fc.replaceAll(";","").replaceAll(RegexTool.getReg("drop table"), "");
		String[] arAll = strAll.trim().split("\\.");
		FileTool.addFile(file,fn+"\t"+arAll[0]+"\t"+arAll[1]);
		return result;
	}
	/**
	 * @author	Tim
	 * @since	2023年11月14日
	 * 
	 * 3.3.
	 * FamilyMart_step02
	 * 列出
	 * 
	 * */
	public static String createLstSPParams() {
		Log.info("createLstSPParams"); 
		String res = "Success";
		
		return res;
	}
}
