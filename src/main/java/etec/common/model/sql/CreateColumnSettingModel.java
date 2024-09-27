package etec.common.model.sql;

import etec.common.utils.RegexTool;

public class CreateColumnSettingModel {

	private String title;

	private String format;

	private String defaultData;

	private String casespecific;

	private String notNull;

	private String character;

	private String other;

	private String setting;

	public CreateColumnSettingModel() {}

	public CreateColumnSettingModel(String setting) {
		String tmpsetting = setting.toUpperCase();
		this.setting = setting;
		this.title = RegexTool.getRegexTargetFirst("TITLE\\s+\\'[^\\']*\\'", setting).replaceAll("TITLE\\s+", "").replaceAll("\\'", "");
		this.format = RegexTool.getRegexTargetFirst("FORMAT\\s+\\'[^\\']*\\'", setting).replaceAll("FORMAT\\s+", "").replaceAll("\\'", "");
		this.defaultData = RegexTool.getRegexTargetFirst("DEFAULT\\s+\\S+", setting).replaceAll("DEFAULT\\s+", "");
		this.casespecific = RegexTool.getRegexTargetFirst("(?:NOT\\s+)?CASESPECIFIC", setting);
		this.notNull = RegexTool.getRegexTargetFirst("NOT\\s+NULL", setting);
		this.character = RegexTool.getRegexTargetFirst("CHARACTER\\s+SET\\s+\\S+", setting).replaceAll("CHARACTER\\s+SET\\s+", "");
		tmpsetting = tmpsetting.replaceAll("TITLE\\s+\\'[^\\']*\\'","");
		tmpsetting = tmpsetting.replaceAll("FORMAT\\s+\\'[^\\']*\\'","");
		tmpsetting = tmpsetting.replaceAll("DEFAULT\\s+\\S+","");
		tmpsetting = tmpsetting.replaceAll("NOT\\s+CASESPECIFIC","");
		tmpsetting = tmpsetting.replaceAll("CASESPECIFIC","");
		tmpsetting = tmpsetting.replaceAll("NOT\\s+NULL","");
		tmpsetting = tmpsetting.replaceAll("CHARACTER\\s+SET\\s+\\S+","");
		this.other = tmpsetting.replaceAll("\\s+", " ").replaceAll("^,\\s*", "");
	}

	public String getTitle() {
		return title;
	}
	@Override
	public String toString() {
		return setting;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDefaultData() {
		return defaultData;
	}

	public void setDefaultData(String defaultData) {
		this.defaultData = defaultData;
	}

	public String getCasespecific() {
		return casespecific;
	}

	public void setCasespecific(String casespecific) {
		this.casespecific = casespecific;
	}

	public String getNotNull() {
		return notNull;
	}

	public void setNotNull(String notNull) {
		this.notNull = notNull;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}


}
