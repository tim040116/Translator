package etec.common.model.sql;

public class CreateIndexModel {
	
	private boolean isPrimary;
	
	private boolean isUnique;
	
	private String[] column;

	private String str;
	
	public boolean isPrimary() {
		return isPrimary;
	}

	public void setStr(String s) {
		this.str = s;
	}
	
	public String toString() {
		return str;
		
	}
	
	public void setPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	public String[] getColumn() {
		return column;
	}

	public void setColumn(String[] column) {
		this.column = column;
	}

}
