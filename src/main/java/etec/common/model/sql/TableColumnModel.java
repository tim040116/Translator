package etec.common.model.sql;

/**
 * create table 的欄位
 * 
 * @author Tim
 * @version dev
 * @since 2023/04/06
 * 
 */
public class TableColumnModel {

	private String columnName;
	
	private String columnType;
	
	private CreateColumnSettingModel setting;

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

	public CreateColumnSettingModel getSetting() {
		return setting;
	}

	public void setSetting(String setting) {
		this.setting = new CreateColumnSettingModel(setting);
	}
	
}
