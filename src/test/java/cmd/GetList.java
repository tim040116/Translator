package cmd;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import etec.common.model.sql.CreateTableModel;
import etec.common.model.sql.TableColumnModel;
import etec.common.utils.EasyFileTool;
import etec.common.utils.FileTool;
import etec.common.utils.RegexTool;
import etec.sql.wrapper.impl.TeradataSqlModelWrapper;
import etec.src.transducer.SQLTransducer;


/**
 * 產出清單的一次性程式
 * 
 * @author	Tim
 * @since	2023/04/11
 * */
public class GetList {

	/**
	 * 把DDL的檔案轉成物件
	 * 並產出Table中欄位的清單
	 * 
	 * @author	Tim
	 * @since	2023/04/11
	 * */
	public static Map<String,CreateTableModel> getColumnList(){
		Map<String,CreateTableModel> lstm = new HashMap<String,CreateTableModel>();
		try {
			String path = "C:\\Users\\User\\Desktop\\全家\\T0\\20230324_資料提供\\Table Schema Script";
			String txt = "C:\\Users\\User\\Desktop\\SQL.txt";
			EasyFileTool eft = new EasyFileTool();
			TeradataSqlModelWrapper wp = new TeradataSqlModelWrapper();
			
			List<File> lstf = FileTool.readFileList(path);
			for(File f : lstf) {
				eft.startWrite(new File(txt));
				String content = FileTool.readFile(f);
				
				CreateTableModel m = wp.createTable(content);
				if(m.getTableName()==null) {
					continue;
				}
				String dbNm = m.getDatabaseName();
				String tbNm = m.getTableName();
				List<TableColumnModel> lstcm = m.getColumn();
				for(TableColumnModel cm : lstcm) {
					String colNm = cm.getColumnName();
					String colType = cm.getColumnType();
					eft.writeline(dbNm+","+tbNm+","+colNm+",\""+colType+"\"");
				}
				eft.close();
				lstm.put(m.getTableName(), m);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return lstm;
	}
	/**
	 * 取出CTAS,insert select,merge into中
	 * 出現的表以極其會使用到的其他表
	 * 已呈現表之間的關聯性
	 * 
	 * @author	Tim
	 * @since	2023/05/02
	 * */
	public static void getTableJoinList() {
		
		try {
			String rootPath = "D:\\站存\\全家\\T1\\";
			String listFile = "D:\\站存\\全家\\T2\\lst\\table之間的關聯.txt";
			//取得清單
			List<File> lstFile = FileTool.readFileList(rootPath);
			//取得內容
			for(File f : lstFile) {
				String fileName = f.getName();
				String fileParent = f.getPath();
				fileParent = fileParent.replaceAll("D:\\\\站存\\\\全家\\\\T1\\\\", "");
				fileParent = fileParent.replaceAll(fileName, "");
				fileParent = fileParent.replace("\\", "");
				String content = FileTool.readFile(f).toUpperCase();
				content = SQLTransducer.cleanSql(content);
				content = content.replaceAll("--.*", "");
				//取得sql
				for(String sql : content.split(";")) {
					String type = "";
					String targetTable = "";
//					if(!sql.contains("SELECT")) {
//						continue;
//					}
					if(sql.matches("\\s*CREATE\\s+[^;]+")) {
						type = "C";
						targetTable = RegexTool.getRegexTargetFirst("\\s*CREATE\\s+((VOLATILE\\s+)|(MULTISET\\s+))?TABLE\\s+\\S+\\s", sql);
						System.out.println(targetTable);
						targetTable = targetTable.replaceAll("CREATE\\s+((VOLATILE\\s+)|(MULTISET\\s+))?TABLE","").trim();
					}
					else if(sql.matches("\\s*INSERT\\s+INTO\\s+[^;]+")) {
						type = "I";	
						targetTable = RegexTool.getRegexTargetFirst("\\s*INSERT\\s+INTO\\s+\\S+\\s", sql).trim();
						targetTable = targetTable.replaceAll("INSERT\\s+INTO","").trim();
					}
					else if(sql.matches("\\s*MERGE\\s+INTO\\s+[^;]+")) {
						type = "M";
						targetTable = RegexTool.getRegexTargetFirst("\\s*MERGE\\s+INTO\\s+\\S+\\s", sql).trim();
						targetTable = targetTable.replaceAll("MERGE\\s+INTO","").trim();
					}else {
						continue;
					}
					//join 跟 from
					String joinTable = "";
					List<String> lstf = RegexTool.getRegexTarget("FROM\\s+[^\\n\\(\\)]+", sql);
					List<String> lstj = RegexTool.getRegexTarget("JOIN\\s+[^\\s\\(\\)]+", sql);
					List<String> lstu = RegexTool.getRegexTarget("USING\\s+[^\\s\\(\\)]+", sql);
					List<String> lst = new ArrayList<String>();
					lst.addAll(lstf);
					lst.addAll(lstu);
					lst.addAll(lstj);
					for(String data : lst) {
						joinTable+="|"+data
								.replaceAll("((FROM)|(JOIN)|(USING))","")
								.replaceAll(",","|")
								.trim();
					}
					//組清單
					String line = fileParent+"|"+fileName+"|"+type+"|"+targetTable+joinTable;
					line = line.replaceAll("\\|\\|", "|")
							.replaceAll("\\s+\\|", "|")
							.replaceAll("\\s+[A-Z]\\|", "|")
							.replaceAll("\\s+[A-Z]$", "")
							;
					FileTool.addFile(listFile, line.replaceAll("\\|\\|", "|"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("FINISH");
	}
}
