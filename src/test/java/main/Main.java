package main;

import java.util.ArrayList;
import java.util.List;

import etec.common.model.sql.SelectTableModel;
import etec.common.utils.RegexTool;
import st.etec.sql.wrapper.SqlModelWrapper;
import st.etec.sql.wrapper.impl.TeradataSqlModelWrapper;

public class Main {
	
	private static final String QUERY_NAME = "RU_";
	
	public static void main(String[] args) {

		/**
		 * 轉換rollup語法					
		 * 
		 * 
		 * */
		try {
			SqlModelWrapper sw = new TeradataSqlModelWrapper();
			SelectTableModel m = sw.selectTable(content2);
			String sql = "";
			String mainQuery = mainQuery(m);
			List<String> lstRollup = rollupQuery(m);
			sql+=mainQuery;
			for(String join : lstRollup) {
				sql+="\r\n"+join;
			}
			sql = rollupColumn(m,sql);
			System.out.println(sql); 
		} catch (Exception e) { 
			e.printStackTrace();
		}
		
	}
	
	//最外層
	public static String rollupColumn(SelectTableModel m ,String join) {
		String select = "SELECT";
		String splitc = " ";
		String groupBy = "GROUP BY";
		String splitg = " ";
		for(String col : m.getLstGroupBy()) {
			col = col.replaceAll("(ROLLUP\\s*\\()|\\)","");
			groupBy+=splitg+RegexTool.decodeCommaInBracket(col);
			if(" ".equals(splitg)) {
				splitg = ",";
			}
		}
		String orderBy = "ORDER BY";
		String splito = " ";
		for(String col : m.getLstOrderBy()) {
			orderBy+=splito+col;
			if(" ".equals(splito)) {
				splito = ",";
			}
		}
		for(String col : m.getLstColumn()) {
			col = col.replaceAll("[^\\.\\(,]+\\.",QUERY_NAME+"0\\.");
			col = col.replaceAll("COUNT\\s*\\(","SUM(");
			select+="\r\n\t"+splitc+col;
			if(" ".equals(splitc)) {
				splitc = ",";
			}
		}
		//取得column mapping的清單
		int cntRollup = 1;
		for (String item : m.getLstGroupBy()) {
            if (item.contains("ROLLUP")) {
            	String col = item.replaceAll("(ROLLUP\\s*\\()|\\)","");
    			for(String c : col.split("<encodingCode_Comma>")) {
    				String co = c.replaceAll("[^\\.\\(,]+\\.", QUERY_NAME+"0\\.");
    				String cn = c.replaceAll("[^\\.\\(,]+\\.", QUERY_NAME+cntRollup+".");
    				select = select.replaceAll(co, cn);
    			}
    			cntRollup++;
            }
        }
		String sql = select+"\r\n"+join+"\r\n"+groupBy+"\r\n"+orderBy;
		return sql;
	}
	//第一層
	public static String mainQuery(SelectTableModel m ) {
		//第一層
		String mainQuery = "";
		String select = "FROM\r\n(\r\n\tSELECT";
		//column
		String sf = " ";
		for(String col : m.getLstColumn()) {
			select+="\r\n\t\t"+sf+col;
			if(" ".equals(sf)) {
				sf=",";
			}
		}
		//from
		String from ="FROM "+m.getFromTable();
		//group by
		String groupby = "GROUP BY";
		String sg = " ";
		for(String c : m.getLstGroupBy()) {
			String col = c.replaceAll("(ROLLUP\\s*\\()|\\)", "");
			groupby+=sg+RegexTool.decodeCommaInBracket(col);
			if(" ".equals(sg)) {
				sg=",";
			}
		}
		mainQuery = select+"\r\n\t"+from+"\r\n\t"+groupby+"\r\n) "+QUERY_NAME+"0";
		return mainQuery;
	}
	
	//其他層
	public static List<String> rollupQuery(SelectTableModel m ) {
		//取得rollup的清單
		List<String[]> lstrollup = new ArrayList<String[]>();
		for (String item : m.getLstGroupBy()) {
            if (item.contains("ROLLUP")) {
            	String col = item.replaceAll("(ROLLUP\\s*\\()|\\)","");
            	lstrollup.add(col.split("<encodingCode_Comma>"));
            }
        }
		//製作rollup的join
		List<String> lstJoin = new ArrayList<String>();
		int cntRollup = 1;
		
		for(String[] arr : lstrollup) {
			String join = "LEFT JOIN\r\n(\r\n\tSELECT";
			String groupby = "GROUP BY";
			String on = "";
			String split = " ";
			String splitOn=" ON";
			for(String c : arr) {
				//join 欄位
				join+="\r\n\t\t"+split+c;
				groupby+=split+c;
				if(" ".equals(split)) {
					split = ",";
				}
				//on 條件
				String c2 = c.replaceAll("[^\\.]+\\.", "");
				on+="\r\n"+splitOn+" "+QUERY_NAME+"0."+c2+" = COALESCE("+QUERY_NAME+cntRollup+"."+c2+","+QUERY_NAME+"0."+c2+")";
				if(" ON".equals(splitOn)) {
					splitOn = "AND";
				}
			}
			join+="\r\n\tFROM "+m.getFromTable();
			join+="\r\n\t"+groupby;
			join+="\r\n) "+QUERY_NAME+cntRollup;
			join+=on;
			cntRollup++;
			lstJoin.add(join);
		}
		return lstJoin;
	}
	public static String content = "select a.Id,a.empName,a.department,a.department_2,sum(a.salary) salary,count(a.Id) id\r\n" + 
			"from TEST.temp_EmpSalary a\r\n" + 
			"group by a.id,rollup(a.department),rollup(a.empName,a.department_2)\r\n" + 
			"order by a.Id,a.department,a.empName,a.department_2";
	public static String content2 = "SELECT A.TIME_ID,\r\n" + 
			"	A.ORG_ID,\r\n" + 
			"	COALESCE(B.GRP_ID,COALESCE(B.KND_ID,'-1')) AS PRD_ID, \r\n" + 
			"	SUM(ORDER_SALES_CNT) AS ORDER_SALES_CNT,\r\n" + 
			"	SUM(ORDER_SALES_AMT) AS ORDER_SALES_AMT,\r\n" + 
			"	SUM(INPRD_SALES_CNT) AS INPRD_SALES_CNT,\r\n" + 
			"	SUM(INPRD_SALES_AMT) AS INPRD_SALES_AMT   \r\n" + 
			"FROM #TP1_BASIC_MFACT_DETAIL A\r\n" + 
			"JOIN PMART.PRD_DIM B \r\n" + 
			"	ON A.PRD_ID = B.PRD_ID\r\n" + 
			"GROUP BY TIME_ID, ORG_ID,\r\n" + 
			"ROLLUP(B.KND_ID, B.GRP_ID)";
	
}
