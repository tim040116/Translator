package etec.src.sql.azure.wrapper;

import java.util.Collections;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.enums.MultiSetEnum;
import etec.common.model.sql.CreateIndexModel;
import etec.common.model.sql.CreateTableModel;
import etec.common.model.sql.SelectTableModel;
import etec.common.model.sql.TableColumnModel;
import etec.common.utils.RegexTool;

/**
 * 將 teradata 的語法 包成物件
 * 
 * @author Tim
 * @version dev
 * @since 2023/04/06
 * 
 */
public class TeradataSqlModelWrapper{

	/**
	 * @author Tim
	 * @since 2023/04/06
	 * @param String create table的sql語句
	 * @return CreateTableModel create table的物件
	 * 
	 */
	public CreateTableModel createTable(String sql) {
		sql = sql.replaceAll("\"REQUEST TEXT\"", "").trim();
		String tempsql = sql.toUpperCase(Locale.TAIWAN).replaceAll("\\s+", " ");
		CreateTableModel model = new CreateTableModel();

		/**
		 * <p>功能 ：拆解Create table 語法</p>
		 * <p>類型 ：搜尋</p>
		 * <p>修飾詞：gmis</p>
		 * <p>範圍 ：從 CREATE TABLE 到 ;</p>
		 * <h2>群組 ：</h2>
		 * 	1.set : set table設定
		 * 	2.dbNm : 資料庫名稱(可能為空)
		 * 	3.tblNm : 表明稱
		 *  4.tblSetting : 表的設定
		 *  5.col : 欄位資訊
		 *  6.pi : primary index
		 * <h2>異動紀錄 ：</h2>
		 * 2024年4月1日	Tim	建立邏輯
		 * */
		String regex = "(?is)CREATE\\s+(?<set>MULTISET|SET)?\\s+TABLE\\s+"
				+ "(?:(?<dbNm>[^.]+)\\.)?(?<tblNm>[^\\(\\s]+)\\s*"
				+ "(?<tblSetting>[^\\(]+)?"
				+ "\\((?<col>.+?)\\)\\s*"
				+ "(?:PRIMARY\\s+?INDEX\\s*\\((?<pi>[\\w,\\s]+)\\)"
				+ "|NO\\s+PRIMARY\\s+INDEX\\s*)?\\s*;";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(tempsql);
		while(m.find()) {
			//SET TABLE
			String strSet = m.group("set").toUpperCase();
			MultiSetEnum multiSet = "SET".equals(strSet)?MultiSetEnum.SET:"MULTISET".equals(strSet)?MultiSetEnum.MULTI_SET:MultiSetEnum.NULL;
			model.setMultiSet(multiSet);
			//DB,TABLE name
			model.setDatabaseName(m.group("dbNm"));
			model.setTableName(m.group("tblNm"));
			String tblSetting = m.group("tblSetting");
			if(tblSetting != null) {
				model.setTableSetting(m.group("tblSetting").split(","));
			}
			/**
			 * <p>功能 ：切分欄位資訊</p>
			 * <p>類型 ：切分</p>
			 * <p>不屬於括號內的逗號</p>
			 * <h2>異動紀錄 ：</h2>
			 * 2024年4月1日	Tim	建立邏輯
			 * */
			for(String strCol : m.group("col").split(",(?![^\\(\\)]+\\))")) {
				TableColumnModel col = new TableColumnModel();
				/**
				 * <p>功能 ：切分單行欄位資訊</p>
				 * <p>類型 ：切分</p>
				 * <p>修飾詞：m</p>
				 * <p>範圍 ：行頭到行尾</p>
				 * <h2>群組 ：</h2>
				 * 	1.欄位名
				 * 	2.欄位型態
				 * 	3.設定
				 * <h2>異動紀錄 ：</h2>
				 * 2024年4月1日	Tim	建立邏輯
				 * */
				Pattern pLine = Pattern.compile("^\\s*,?\\s*(\\S+)\\s+(\\S+)\\s*(.*),?",Pattern.MULTILINE);
				Matcher mLine = pLine.matcher(strCol);
				while(mLine.find()) {
					col.setColumnName(mLine.group(1));
					col.setColumnType(mLine.group(2));
					col.setSetting(mLine.group(3));
				}
				model.getColumn().add(col);
			}
			/**
			 * <p>功能 ：取得INDEX資訊</p>
			 * <p>類型 ：搜尋</p>
			 * <p>修飾詞：i</p>
			 * <p>範圍 ：PRIMARY|UNIQUE INDEX(.+)</p>
			 * <h2>群組 ：</h2>
			 * 	1.primary 跟 unique 設定
			 * 	2.index 欄位
			 * <h2>異動紀錄 ：</h2>
			 * 2024年4月1日	Tim	建立邏輯
			 * */
			String regIndex = "((?:PRIMARY\\s+|UNIQUE\\s+)*)INDEX\\s*\\(([^\\)]+)\\)";
			Pattern pIndex = Pattern.compile(regIndex,Pattern.CASE_INSENSITIVE);
			Matcher mIndex = pIndex.matcher(m.group(0));
			while(mIndex.find()) {
				CreateIndexModel indexModel = new CreateIndexModel();
				indexModel.setStr(mIndex.group(0));
				if(mIndex.group(1)!=null) {
					indexModel.setPrimary(mIndex.group(1).toUpperCase().contains("PRIMARY"));
					indexModel.setUnique(mIndex.group(1).toUpperCase().contains("UNIQUE"));
				}
				String[] arrIndex = mIndex.group(2).trim().split("\\s*,\\s*");
				indexModel.setColumn(arrIndex);
				model.getIndex().add(indexModel);
			}
			//partition by
			model.getWithSetting().setPartition(m.group(0).replaceAll("(?i)PARTITION\\s+BY\\s*([^\\)]+)\\)","$1"));
		}
		return model;
	}

	/**
	 * @author Tim
	 * @since 2023/04/28
	 * @param String select table的sql語句
	 * @return SelectTableModel select table的物件
	 * 
	 */
	public SelectTableModel selectTable(String sql) {
		String step = "";
		String temp = "";
		int bracketsCnt = 0;
		SelectTableModel m = new SelectTableModel();
		// 用迴圈取得關鍵字
		for (String c : sql.trim().toUpperCase().split("")) {
			switch (step) {
			case "":// 最一開始
				temp += c;
				if (temp.matches("\\s*SELECT")) {
					temp = "";
					step = "SELECT";
				}
				break;
			case "SELECT":// 欄位
				// 判斷是否在方法之中
				bracketsCnt += "(".equals(c) ? 1 : ")".equals(c) ? -1 : 0;
				// 進下一階段
				if (temp.matches("[^;]*FROM$") && bracketsCnt == 0) {
					m.getLstColumn().add(temp.replaceAll("FROM", "").trim());
					temp = "";
					step = "FROM";
					continue;
				}
				// 完成一個欄位
				if (",".equals(c) && bracketsCnt == 0) {
					m.getLstColumn().add(temp.trim());
					temp = "";
				} else {
					temp += c;
				}
				break;
			case "FROM":// 判斷from是否為子查詢
				if (c.matches("\\s")) {
					continue;
				}
				temp += c;
				step = "(".equals(c) ? "FROM_QUERY" : "FROM_TABLE";
				if("(".equals(c)) {
					bracketsCnt = 1;
				}
				continue;
			case "FROM_TABLE":// from table
				temp += c;
				if ("\n".equals(c)) {
					m.setFromTable(temp.trim());
					temp = "";
					step = "CLASSIFY";
					continue;
				}
				break;
			case "FROM_QUERY":// from sub query
				bracketsCnt += "(".equals(c) ? 1 : ")".equals(c) ? -1 : 0;
				break;
			case "CLASSIFY":// 區分語法為哪一類
				if (temp.matches("\\s*(\\S+\\s+)?JOIN")) {
					step = "JOIN";
				} else if (temp.matches("\\s*GROUP BY")) {
					step = "GROUP_BY";
				} else if (temp.matches("\\s*ORDER BY")) {
					step = "ORDER_BY";
				} else {
					temp += c;
				}
				break;
			case "JOIN":
				
				break;
			case "GROUP_BY":
				temp += c;
				if ("\n".equals(c)) {
					Collections.addAll(m.getLstGroupBy(),
							RegexTool.encodeCommaInBracket(temp.replaceAll("GROUP\\s+BY", "").trim()).split(","));
					temp = "";
					step = "CLASSIFY";
					continue;
				}
				break;
			case "ORDER_BY":
				temp += c;
				if ("\n".equals(c)) {
					Collections.addAll(m.getLstOrderBy(), temp.replaceAll("ORDER\\s+BY", "").trim().split(","));
					temp = "";
					step = "CLASSIFY";
					continue;
				}
				break;
			default:
				break;
			}
		}
		// 結尾
		if (!"".equals(temp)) {
			if (step == "ORDER_BY") {
				Collections.addAll(m.getLstOrderBy(), temp.replaceAll("ORDER\\s+BY", "").trim().split(","));
				temp = "";
			} else if (step == "GROUP_BY") {
				Collections.addAll(m.getLstGroupBy(), temp.replaceAll("GROUP\\s+BY", "").trim().split(","));
				temp = "";
			}
		}
		return m;
	}
}
