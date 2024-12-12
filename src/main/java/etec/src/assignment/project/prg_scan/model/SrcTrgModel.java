package etec.src.assignment.project.prg_scan.model;

import java.util.List;

public class SrcTrgModel {
	
	private String fileName;

	private String type;

	private String trgTable;

	private List<String> srcTable;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTrgTable() {
		return trgTable;
	}

	public void setTrgTable(String trgTable) {
		this.trgTable = trgTable;
	}

	public List<String> getSrcTable() {
		return srcTable;
	}

	public void setSrcTable(List<String> srcTable) {
		this.srcTable = srcTable;
	}
}
