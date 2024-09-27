package etec.src.translator.sql.az.wrapper;

import java.util.Collections;
import java.util.Locale;

import etec.common.model.sql.CreateIndexModel;
import etec.common.model.sql.CreateTableModel;
import etec.common.model.sql.SelectTableModel;
import etec.common.model.sql.TableColumnModel;
import etec.common.utils.RegexTool;
import etec.framework.context.translater.enums.MultiSetEnum;

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
		String temp = "";// 暫存字串
		int parenthesesCnt = 0;// 括號記數
		int step = 0;// 步驟
		boolean flag = false;
		CreateTableModel model = new CreateTableModel();

		// 解析SQL 語法
		for (String c : tempsql.split("")) {

			switch (step) {
			case 0:// create
				if ("".equals(c)) {
					continue;
				}
				if ("CREATE".equals(temp.trim())) {
					step = 1;
					temp = "";
					continue;
				}
				temp += c;
				break;
			case 1:// multiSet + table name
				temp += c;
				if (temp.matches("(MULTISET|SET)?\\s*TABLE\\s+\\S+\\s+")) {
					// Multi set
					if (temp.contains("MULTISET TABLE")) {
						model.setMultiSet(MultiSetEnum.MULTI_SET);
					} else if (temp.contains("SET TABLE")) {
						model.setMultiSet(MultiSetEnum.SET);
					} else {
						model.setMultiSet(MultiSetEnum.MULTI_SET);
					}
					// table name
					String[] table = temp.replaceAll("\\s*(MULTISET|SET)?\\s+TABLE\\s+", "").trim().split("\\.");
					if (table.length > 1) {
						model.setDatabaseName(table[table.length - 2]);
					}
					model.setTableName(table[table.length - 1]);
					temp = "";
					step = 2;
					continue;
				}
				break;
			case 2:// table setting
				if ("(".equals(c)) {
					String[] arrSetting = temp.replaceAll("^\\s*,\\s*", "").split(",");
					model.setTableSetting(arrSetting);
					temp = "";
					step = 3;
					parenthesesCnt = 1;
					continue;
				}
				temp += c;
				break;
			case 3:// column
					// 計算括號
				if ("(".equals(c)) {
					parenthesesCnt++;
				} else if (")".equals(c)) {
					parenthesesCnt--;
				}
				if("'".equals(c)) {
					flag = !flag;
				}
				if ((",".equals(c) && parenthesesCnt == 1 || parenthesesCnt == 0)&&!flag) {
					TableColumnModel col = new TableColumnModel();
					String[] arCol = temp.replaceAll("^,\\s+", "").trim().split(" ");
					col.setColumnName(arCol[0]);
					col.setColumnType(arCol[1]);
					col.setSetting(temp.replaceAll(arCol[0], "").replace(arCol[1], "").trim());
					model.getColumn().add(col);
					temp = "";
					if (parenthesesCnt == 0) {
						step = 4;
						continue;
					}
				}
				temp += c;
				break;
			case 4:// 判斷接下來要進入的階段
				temp += c;
				if ("PARTITION BY".equals(temp)) {
					step = 5;
					continue;
				} else if (temp.contains("INDEX")) {
					step = 6;
					continue;
				}
				break;
			case 5:// PARTITION BY
					// 計算範圍
				temp += c;
				if ("(".equals(c)) {
					parenthesesCnt++;
				} else if (")".equals(c)) {
					parenthesesCnt--;
					if (parenthesesCnt == 0) {
						model.getWithSetting().setPartition(temp);
						temp = "";
						step = 4;
						continue;
					}
				}
				break;
			case 6:// index
					// 計算範圍
				temp += c;
				if ("(".equals(c)) {
					parenthesesCnt++;
				} else if (")".equals(c)) {
					parenthesesCnt--;
					if (parenthesesCnt == 0) {
						CreateIndexModel indexModel = new CreateIndexModel();
						indexModel.setStr(temp);
						indexModel.setPrimary(temp.contains("PRIMARYE "));
						indexModel.setUnique(temp.contains("UNIQUE "));
						RegexTool.getRegexTargetFirst("\\([^\\)]+", temp);
						String[] arrIndex = temp.replaceAll("(UNIQUE\\s+)?(PRIMARY\\s+)?\\s?INDEX\\s*\\(", "")
								.replaceAll("\\)$", "").trim().split(",");
						indexModel.setColumn(arrIndex);
						model.getIndex().add(indexModel);
						temp = "";
						step = 4;
						continue;
					}
				}
				break;
			default:
				break;
			}
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
