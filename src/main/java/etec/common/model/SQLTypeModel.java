package etec.common.model;

import etec.framework.context.translater.enums.SQLTypeEnum;

public class SQLTypeModel {

	private SQLTypeEnum type;
	
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public SQLTypeEnum getType() {
		return type;
	}

	public void setType(String type) {
		switch(type.trim().toUpperCase()){
			case "DDL":
				this.type = SQLTypeEnum.DDL;
				break;
			case "DQL":
				this.type = SQLTypeEnum.DQL;
				break;
			case "DML":
				this.type = SQLTypeEnum.DML;
				break;
			case "OTHER":
				this.type = SQLTypeEnum.OTHER;
				break;
			case "ELSE":
				this.type = SQLTypeEnum.ELSE;
				break;
		}
	}
}
