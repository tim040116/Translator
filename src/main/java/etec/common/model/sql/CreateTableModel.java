package etec.common.model.sql;

import java.util.ArrayList;
import java.util.List;

import etec.common.enums.MultiSetEnum;

/**
 * create table的語法模組
 * 
 * @author Tim
 * @version dev
 * @since 2023/04/06
 * 
 */
@SQLModel
public class CreateTableModel {

	private String databaseName;
	
	private String tableName;
	
	private MultiSetEnum multiSet;
	
	private String[] tableSetting;
	
	private List<TableColumnModel> column = new ArrayList<TableColumnModel>();
	
	private TableWithSettingModel withSetting = new TableWithSettingModel();
	
	private List<CreateIndexModel> index = new ArrayList<CreateIndexModel>();
	
	public String getDatabaseTable() {
		return databaseName==null?tableName:(databaseName+"."+tableName);
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public MultiSetEnum getMultiSet() {
		return multiSet;
	}

	public void setMultiSet(MultiSetEnum multiSet) {
		this.multiSet = multiSet;
	}

	public String[] getTableSetting() {
		return tableSetting;
	}

	public void setTableSetting(String[] tableSetting) {
		this.tableSetting = tableSetting;
	}

	public List<TableColumnModel> getColumn() {
		return column;
	}

	public void setColumn(List<TableColumnModel> column) {
		this.column = column;
	}

	public TableWithSettingModel getWithSetting() {
		return withSetting;
	}

	public void setWithSetting(TableWithSettingModel withSetting) {
		this.withSetting = withSetting;
	}

	public List<CreateIndexModel> getIndex() {
		return index;
	}

	public void setIndex(List<CreateIndexModel> index) {
		this.index = index;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

}
