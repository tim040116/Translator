package etec.src.translator.project.azure.fm.hist_export.model;

public class CreateExpTPTModel {
	
	private String fileName;
	
	private String tdSchema;
	
	private String tdTable;
	
	private String where;
	
	private String dir;
	
	private CreateExpTPTTableModel table;

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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public CreateExpTPTTableModel getTable() {
		return table;
	}

	public void setTable(CreateExpTPTTableModel table) {
		this.table = table;
	}
	
}
