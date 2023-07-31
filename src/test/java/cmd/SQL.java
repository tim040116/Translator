package cmd;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import etec.common.model.sql.CreateTableModel;
import etec.common.utils.EasyFileTool;
import etec.common.utils.FileTool;

/**
 * 轉換SQL的一次性程式
 * 
 * @author	Tim
 * @since	2023/04/11
 * */
public class SQL {

	/**
	 * 
	 * 應對Justin的無能
	 * 他覺得將資料copy into到DB時欄位型態的對應處理太麻煩
	 * 就全部開成NVARCHAR(MAX)然後叫我們幫他轉換到DB
	 * 還沒給錢，態度還恨傲慢
	 * 但我們還是得幫他做...
	 * 
	 * 
	 * 將DDL物件與Copy into的Table做Mapping後
	 * 轉成為insert select的語法
	 * 
	 * @author	Tim
	 * @since	2023/04/12
	 * */
	public static void writeInsertSelect(){
		try {
			Map<String,CreateTableModel> colList = GetList.getColumnList();
			String path = "C:\\Users\\User\\Desktop\\selectTable.txt";
			String res = "C:\\Users\\User\\Desktop\\SQL2.txt";
			String strf = FileTool.readFile(new File(path));
			EasyFileTool eft = new EasyFileTool();
			eft.startWrite(new File(res));
			for(String s : strf.split(";")) {
				if(s.matches("\\s*")) {
					continue;
				}
				StringBuffer sb = new StringBuffer();
				String[] sql = s.trim().toUpperCase().split("\r\n");
				String column[] = sql[0].replaceAll("SELECT", "").replaceAll("\\s+", "").split(",");
				String table = sql[1].replaceAll("FROM", "").trim();
				CreateTableModel az = colList.get(table);
				try {
					sb.append("INSERT INTO "+az.getDatabaseTable()+" SELECT\r\n");
				}catch (Exception e) {
					System.out.println();
				}
				eft.writeline("storing."+table);
				for(int i=0;i<column.length;i++) {
					sb.append(i==0?" ":",");
					String azcol = az.getColumn().get(i).getColumnType();
					String stcol = column[i];
					azcol = azcol
							.replaceAll("TIMESTAMP\\s*\\(\\s*[0-9]+\\s*\\)", "DATETIME")
							.replaceAll("VARBYTE", "VARBINARY")
							.replaceAll("NUMBER$", "NUMERIC")
							;
					if(azcol.matches("VARCHAR\\s*\\(\\s*[0-9]+\\s*\\)")) {
						stcol = "TRIM("+stcol+")";
					}
					sb.append("CAST("+stcol+" AS "+azcol+")\r\n");
					eft.writeline(","+stcol+",,"+az.getColumn().get(i).getColumnName()+",\""+azcol+"\"");
				}
				sb.append("FROM STAGING."+table+";");
				
				
				eft.writeline(sb.toString());
			}
			eft.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
