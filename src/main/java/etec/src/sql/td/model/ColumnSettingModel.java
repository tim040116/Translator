package etec.src.sql.td.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.utils.RegexTool;

/**
 * <h1>Create Table 中欄位的設定</h1>
 * <p>包裝CREATE TABLE時各欄位的設定值</p>
 * <h2>屬性</h2>
 * <p>
 * 	<br>String {@link #title}
 * 	<br>String {@link #format}
 * 	<br>String {@link #defaultData}
 * 	<br>String {@link #casespecific}
 * 	<br>String {@link #nullable}
 * 	<br>String {@link #characterSet}
 * </p>
 * <h2>方法</h2>
 * <p>
 * 	<br>{@link #ColumnSettingModel()}
 * 	<br>{@link #ColumnSettingModel(String)}
 * </p>
 * <h2>異動紀錄</h2>
 * <br>2024年3月14日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		TableColumnModel
 */
public class ColumnSettingModel {

	//	欄位的敘述
	private String title = "";
	
	//	日期欄位的格式
	private String format = "";
	
	//	預設值
	private String defaultData = "";
	
	//	是否區分大小寫
	private String casespecific = "";
	
	//	可否為空值
	private String nullable = "";
	
	//	語系
	private String characterSet = "";
	
	//	PK
	private String primaryIndex = "";
	public ColumnSettingModel() {};
	/**
	 * <h1>包裝CREATE TABLE時各欄位的設定值</h1>
	 * <p>整段設定放進來，可以直接解析，包裝</p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月14日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	setting	設定值
	 * @throws	
	 * @see
	 * @return	
			 */
	public ColumnSettingModel(String setting) {
		/**
		 * <p>功能 ：分解欄位設定資訊</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：i</p>
		 * <p>範圍 ：欄位型態後面的設定 </p>
		 * <h2>群組 ：</h2>
		 * 	1.format : format_'' 單引號裡的字串
		 * 	2.cssp : CASESPECIFIC 或 NOT_CASESPECIFIC
		 * 	3.nullable : NULLABLE 或 NOT_NULLABLE
		 *  4.chrSet : CHARACTER_SET_後面的字串
		 *  5.title : TITLE_'' 單引號裡的字串
		 *  6.def : DEFAULT_後面的字串
		 * <h2>備註 ：</h2>
		 * 	用or合併成一次查詢，導致每個find只有一個group裡面會有值
		 *  其餘的都會是null，
		 *  不同設定會放在不同find中，不是group
		 * <h2>異動紀錄 ：</h2>
		 * 2024年3月13日	Tim	建立邏輯
		 * */
		String regCol = 
				  "(?<=FORMAT_')(?<format>[^']+)"
				+ "|(?<cssp>(?:NOT_)?CASESPECIFIC)"
				+ "|(?<nullable>(?:NOT_)?NULL)"
				+ "|(?<=CHARACTER_SET_)(?<chrSet>\\S+)"
				+ "|(?<=TITLE_')(?<title>[^']+)"
				+ "|(?<=DEFAULT_)(?<def>\\S+)"
				;
		Pattern pCol = Pattern.compile(regCol,Pattern.CASE_INSENSITIVE);
		Matcher mCol = pCol.matcher(setting);
		while(mCol.find()) {
			title = mCol.group("title") != null ?mCol.group("title") :title;
			format = mCol.group("format") != null ?mCol.group("format") :format;
			defaultData = mCol.group("def") != null ?mCol.group("def") :defaultData;
			casespecific = mCol.group("cssp") != null ?mCol.group("cssp") :casespecific;
			nullable = mCol.group("nullable") != null ?mCol.group("nullable") :nullable;
			characterSet = mCol.group("chrSet") != null ?mCol.group("chrSet") :characterSet;
		}
	}
	public String getTitle() {
		return title;
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
	public String getNullable() {
		return nullable;
	}
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
	public String getCharacterSet() {
		return characterSet;
	}
	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	@Override
	public String toString() {
		return    " TITLE '" + title + "'"
				+ " FORMAT '" + format + "'"
				+ " DEFAULT " + defaultData
				+ " " + casespecific 
				+ " " + nullable 
				+ " CHARACTER SET " + characterSet 
				+ " ";
	}
	public String getPrimaryIndex() {
		return primaryIndex;
	}
	public void setPrimaryIndex(String primaryIndex) {
		this.primaryIndex = primaryIndex;
	}
}
