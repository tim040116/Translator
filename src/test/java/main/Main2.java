package main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author	Tim
 * @since	2023年10月11日
 *
 *
 * */
public class Main2 {

	static String txt = "CREATE MULTISET TABLE ${STAGE}.AC_RMMMDDI (\r\n"
			+ "	 \"STNO\"				VARCHAR(6)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"REMD_DATE\"		VARCHAR(7)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"CSRNUM\"			VARCHAR(5)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"WETHR\"			VARCHAR(2)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT1\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT2\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT3\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT4\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT5\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT6\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT7\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT8\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT9\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT10\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT11\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT12\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT13\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"DEPAMT14\"			VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"RVN\"				VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ "	,\"TKSL\"				VARCHAR(10)CHARACTER SET LATIN NOT CASESPECIFIC\r\n"
			+ ")  NO PRIMARY INDEX;";

	
	public static void main(String[] args) {
		String res = "";
		txt = txt.replaceAll("(?i)(?:MULTI)?SET", "");
		String regsql = "(?i)CREATE\\s+TABLE\\s+([^\\s()]+)(?:\\s+AS)?\\s*\\(\\s*+([\\S\\s]+?)\\s*\\)[^;()]*;";
		Matcher msql = Pattern.compile(regsql).matcher(txt);
		while(msql.find()) {
			//取得參數
			String tableNm = msql.group(1).replaceAll("[^.]+\\.([^.]+)", "$1");
			String strCol  = msql.group(2);
			//處理欄位
			int totlen = 1;
			List<String> lstCol = new ArrayList<String>();
			String regcol = "(?i)\\\"([^\\\"]+)\\\"\\s*+VARCHAR\\s*\\(\\s*(\\d+)\\s*\\)";
			Matcher mcol = Pattern.compile(regcol).matcher(strCol);
			while(mcol.find()) {
				//取得參數
				String colNm = mcol.group(1).toLowerCase();
				int len = Integer.parseInt(mcol.group(2));
				String str = "substring(data_row,"+totlen+","+len+") as "+colNm;
				totlen += len;
				lstCol.add(str);
			}
			res +="\r\n"
				+ "IF OBJECT_ID('dev.stg_"+ tableNm +"_02','U') IS NOT　NULL\r\n"
				+ "BEGIN\r\n"
				+ "DROP TABLE dev.stg_" + tableNm + "_02 ;\r\n"
				+ "END\r\n"
				+ "CREATE TABLE dev.stg_" + tableNm + "_02 \r\n"
				+ "WITH (\r\n"
				+ "\t CLUSTERED COLUMNSTORE INDEX\r\n"
				+ "\t,DISTRIBUTION = REPLICATE\r\n"
				+ ")\r\n"
				+ "AS\r\n"
				+ "SELECT\r\n"
				+ "\t " + String.join("\r\n\t,", lstCol) + "\r\n"
				+ "FROM stg_" + tableNm + "_01 ; \r\n"
				
			;
		}
		
		System.out.println(res);
	}

}
