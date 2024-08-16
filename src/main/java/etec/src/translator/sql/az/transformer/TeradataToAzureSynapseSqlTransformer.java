package etec.src.translator.sql.az.transformer;

import etec.common.model.sql.CreateIndexModel;
import etec.common.model.sql.CreateTableModel;
import etec.common.model.sql.TableColumnModel;

/**
 * 將TD語法轉換成AZ
 * 
 * @author Tim
 * @version dev
 * @since 2023/04/06
 * 
 */
public class TeradataToAzureSynapseSqlTransformer{

	public String transformCreateTable(CreateTableModel m) {
		String res = "";
		
		StringBuffer sb = new StringBuffer();
		//drop if exist
		sb.append("\r\nIF OBJECT_ID(N'"+m.getTableName()+"') IS NOT NULL");
		sb.append("\r\nDROP TABLE "+m.getTableName()+";");
		//create table
		sb.append("\r\nCREATE TABLE "+m.getTableName()+" (");
		//column
		String column = "";
		for(TableColumnModel col : m.getColumn()) {
			column+="\r\n\t"+col.getColumnName();
			column+=" "+replaceColumnType(col.getColumnType());
			column+=" "+replaceColumnSetting(col.getSetting().toString());
			column+=",";
		}
		sb.append(column.replaceAll(",$", ""));
		//with
		sb.append("\r\nWITH (");
		sb.append(replaceWith(m));
		sb.append("\r\n);");
		return res;
	}
	
	//欄位型態轉換
	private String replaceColumnType(String sql) {
		String result = sql.trim()
				.replaceAll("TIMESTAMP\\s*\\(\\s*[0-9]+\\s*\\)", "DATETIME")
				.replaceAll("VARBYTE", "VARBINARY")
				.replaceAll("NUMBER$", "NUMERIC")
				;
		return result;
	}
	//欄位設定轉換
	private String replaceColumnSetting(String sql) {
		String result = sql
				.replaceAll("CHARACTER SET \\S+", " ")
				.replaceAll("NOT CASESPECIFIC", " ")
				.replaceAll("TITLE\\s+'[^']+'", " ")
				.replaceAll("\\s*FORMAT\\s+'[^']+'\\s*", " ")
				;
		return result;
	}
	//with 設定
	private String replaceWith(CreateTableModel m) {
		String res = "";
		res+="\r\n\tCLUSTERED COLUMNSTORE INDEX,";
		String distribution = "ROUND_ROBIN";
		if(!m.getIndex().isEmpty()) {
			String hash = "";
			for(CreateIndexModel im : m.getIndex()) {
				for(String index : im.getColumn()) {
					hash+=index+("".equals(hash)?"":",");
				}
			}
			distribution = "HASH("+hash+")";
		}
		res+="\r\n\tDISTRIBUTION = "+distribution;
		return res;
	}
}
