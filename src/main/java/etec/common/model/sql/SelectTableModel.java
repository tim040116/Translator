package etec.common.model.sql;

import java.util.ArrayList;
import java.util.List;

public class SelectTableModel {
	
	private boolean distinct;
	
	private List<String> lstColumn = new ArrayList<String>();
	
	private String fromTable = "";
	
	private List<String> lstOrderBy = new ArrayList<String>();
	
	private List<String> lstGroupBy = new ArrayList<String>();

	public List<String> getLstColumn() {
		return lstColumn;
	}

	public void setLstColumn(List<String> lstColumn) {
		this.lstColumn = lstColumn;
	}

	public String getFromTable() {
		return fromTable;
	}

	public void setFromTable(String fromTable) {
		this.fromTable = fromTable;
	}

	public List<String> getLstOrderBy() {
		return lstOrderBy;
	}

	public void setLstOrderBy(List<String> lstOrderBy) {
		this.lstOrderBy = lstOrderBy;
	}

	public List<String> getLstGroupBy() {
		return lstGroupBy;
	}

	public void setLstGroupBy(List<String> lstGroupBy) {
		this.lstGroupBy = lstGroupBy;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
}
