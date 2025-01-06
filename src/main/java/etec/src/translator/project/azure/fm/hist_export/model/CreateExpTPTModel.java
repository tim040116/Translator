package etec.src.translator.project.azure.fm.hist_export.model;

import java.util.LinkedList;
import java.util.List;

public class CreateExpTPTModel {
	
	private String tdSchema;
	
	private String tdTable;
	
	private List<String> tdCol = new LinkedList<String>();
	
	private String azSchema;
	
	private String azTable;
	
	private List<String> azCol = new LinkedList<String>();
	
	private String where;
	
	private String dir;
	
	private List<String> lstHeader = new LinkedList<String>();
	
	private List<String> lstSelect = new LinkedList<String>();

	public List<String> getLstHeader() {
		return lstHeader;
	}

	public void setLstHeader(List<String> lstHeader) {
		this.lstHeader = lstHeader;
	}

	public List<String> getLstSelect() {
		return lstSelect;
	}

	public void setLstSelect(List<String> lstSelect) {
		this.lstSelect = lstSelect;
	}

	public String getTdSchema() {
		return tdSchema;
	}

	public void setTdSchema(String tdSchema) {
		this.tdSchema = tdSchema;
	}

	public String getTdTable() {
		return tdTable;
	}

	public void setTdTable(String tdTable) {
		this.tdTable = tdTable;
	}

	public List<String> getTdCol() {
		return tdCol;
	}

	public void setTdCol(List<String> tdCol) {
		this.tdCol = tdCol;
	}

	public String getAzSchema() {
		return azSchema;
	}

	public void setAzSchema(String azSchema) {
		this.azSchema = azSchema;
	}

	public String getAzTable() {
		return azTable;
	}

	public void setAzTable(String azTable) {
		this.azTable = azTable;
	}

	public List<String> getAzCol() {
		return azCol;
	}

	public void setAzCol(List<String> azCol) {
		this.azCol = azCol;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		if("".equals(dir)) {
			dir = "D:\\JobServer\\DATA\\HIST_BACKUP\\TEST\\";
		}
		this.dir = dir;
	}
	
}
