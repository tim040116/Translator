package etec.src.sql.td.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>欄位的物件</h1>
 * <p></p>
 * <h2>屬性</h2>
 * <p>
 * 	<br>String {@link #columnName}
 * 	<br>String {@link #columnType}
 * 	<br>ColumnSettingModel {@link #setting}
 * </p>
 * <h2>方法</h2>
 * <p>
 * 	<br>static Map {@link #convertToMap(String)}
 * </p>
 * <h2>異動紀錄</h2>
 * <br>2024年3月14日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		ColumnSettingModel
 */
public class TableColumnModel {

	private String columnName;
	
	private String columnType;
	
	private ColumnSettingModel setting;

	public TableColumnModel() {}
	public TableColumnModel(String columnScript) {
		/**
		 * <p>功能 ：切分單行欄位資訊</p>
		 * <p>類型 ：切分</p>
		 * <p>修飾詞：m</p>
		 * <p>範圍 ：行頭到行尾</p>
		 * <h2>群組 ：</h2>
		 * 	1.欄位名
		 * 	2.欄位型態
		 * 	3.設定
		 * <h2>異動紀錄 ：</h2>
		 * 2024年3月14日	Tim	建立邏輯
		 * */
		Pattern pLine = Pattern.compile("^\\s*,?\\s*(\\S+)\\s+(\\S+)\\s*(.*),?",Pattern.MULTILINE);
		Matcher mLine = pLine.matcher(columnScript);
		while(mLine.find()) {
			this.columnName = mLine.group(1);
			this.columnType = mLine.group(2);
			this.setting = new ColumnSettingModel(mLine.group(3));
		}
	}
	public TableColumnModel(String colNm,String colType,String columnScript) {
		this.columnName = colNm;
		this.columnType = colType;
		this.setting = new ColumnSettingModel(columnScript);
	}
	/**
	 * <h1>建立欄位的Map</h1>
	 * <p>將欄位的語法切分，組裝並包成Map</p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月14日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	columnScript	CREATE TABLE語法中整段欄位的法
	 * @throws	
	 * @see
	 * @return	<欄位名,物件>
			 */
	public static Map<String,TableColumnModel> convertToMap(String columnScript){
		Map<String,TableColumnModel> resMap = new HashMap<String,TableColumnModel>();
		/**
		 * <p>功能 ：切分單行欄位資訊</p>
		 * <p>類型 ：切分</p>
		 * <p>修飾詞：m</p>
		 * <p>範圍 ：行頭到行尾</p>
		 * <h2>群組 ：</h2>
		 * 	1.欄位名
		 * 	2.欄位型態
		 * 	3.設定
		 * <h2>異動紀錄 ：</h2>
		 * 2024年3月14日	Tim	建立邏輯
		 * */
		Pattern pLine = Pattern.compile("^\\s*,?\\s*(\\S+)\\s+(\\S+)\\s*(.*),?",Pattern.MULTILINE);
		Matcher mLine = pLine.matcher(columnScript);
		while(mLine.find()) {
			TableColumnModel m = new TableColumnModel(mLine.group(1),mLine.group(2),mLine.group(3));
			resMap.put(mLine.group(1).toUpperCase(), m);
		}
		return resMap;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public ColumnSettingModel getSetting() {
		return setting;
	}
	public void setSetting(ColumnSettingModel setting) {
		this.setting = setting;
	}
	@Override
	public String toString() {
		return columnName + " " + columnType + " " + setting;
	}

}
