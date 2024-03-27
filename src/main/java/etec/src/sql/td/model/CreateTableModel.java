package etec.src.sql.td.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.enums.MultiSetEnum;

/**
 * create table的語法模組
 * 
 * @author Tim
 * @version dev
 * @since 2023/04/06
 * 
 */
public class CreateTableModel {
	
	private MultiSetEnum multiSet = MultiSetEnum.MULTI_SET;

	private String databaseName;
	
	private String tableName;
	
	private String setting;
	
	private List<TableColumnModel> column = new ArrayList<TableColumnModel>();
	
	public CreateTableModel() {}
		
	public CreateTableModel(String script) {
		/**
		 * <p>功能 ：拆解Create table 語法</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：gmis</p>
		 * <p>範圍 ：從 CREATE TABLE 到 ;</p>
		 * <h2>群組 ：</h2>
		 * 	1.set : set table設定
		 * 	2.dbNm : 資料庫名稱(可能為空)
		 * 	3.tblNm : 表明稱
		 *  4.tblSetting : 表的設定
		 *  5.col : 欄位資訊
		 *  6.pi : primary index
		 * <h2>異動紀錄 ：</h2>
		 * 2024年3月27日	Tim	建立邏輯
		 * */
		String regex = "(?is)CREATE\\s+(?<set>MULTISET|SET)?\\s+TABLE\\s+"
				+ "(?:(?<dbNm>[^.]+)\\.)?(?<tblNm>[^\\(\\s]+)\\s*"
				+ "(?<tblSetting>[^\\(]+)?"
				+ "\\((?<col>.+?)\\)\\s*"
				+ "(?:PRIMARY\\s+?INDEX\\s*\\((?<pi>[\\w,\\s]+)\\)"
				+ "|NO\\s+PRIMARY\\s+INDEX\\s*)?\\s*;";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(script);
		while(m.find()) {
			String strSet = m.group("set").toUpperCase();
			multiSet = "SET".equals(strSet)?MultiSetEnum.SET:"MULTISET".equals(strSet)?MultiSetEnum.MULTI_SET:MultiSetEnum.NULL;
			databaseName = m.group("dbNm");
			tableName = m.group("tblNm");
			setting = m.group("tblSetting");
			/**
			 * <p>功能 ：切分欄位資訊</p>
			 * <p>類型 ：切分</p>
			 * <p>不屬嫆括號內的逗號</p>
			 * <h2>異動紀錄 ：</h2>
			 * 2024年3月27日	Tim	建立邏輯
			 * */
			for(String col : m.group("col").split(",(?![^\\(\\)]+\\))")) {
				column.add(new TableColumnModel(col));
			}
		}
	}

}
