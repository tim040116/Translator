package etec.src.translator.project.azure.fm.export.model;

import java.util.ArrayList;
import java.util.List;

public class TableModel {

	/**database name*/
	private String dbNm;
	/**table name*/
	private String tblNm;
	/**where 條件*/
	private String condition;
	/**路徑*/
	private String filePath;
	/**檔名*/
	private String fileNm;
	/**column*/
	private List<String> lstColumn = new ArrayList<String>();
	
	public boolean match(String dbNm,String tblNm) {
		return dbNm.trim().toLowerCase().equals(this.dbNm)&&tblNm.trim().toLowerCase().equals(this.tblNm);
	}
	
	public String getDbNm() {
		return dbNm;
	}
	public void setDbNm(String dbNm) {
		this.dbNm = dbNm;
	}
	public String getTblNm() {
		return tblNm;
	}
	public void setTblNm(String tblNm) {
		this.tblNm = tblNm;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileNm() {
		return fileNm;
	}
	public void setFileNm(String fileNm) {
		this.fileNm = fileNm;
	}
	public List<String> getLstColumn() {
		return lstColumn;
	}
	public void setLstColumn(List<String> lstColumn) {
		this.lstColumn = lstColumn;
	}
	
	
}
