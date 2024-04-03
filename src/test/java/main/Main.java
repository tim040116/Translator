package main;

import etec.common.utils.excel.Excel;
import etec.src.sql.azure.wrapper.TeradataSqlModelWrapper;
/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\familymart\\T1\\SQLAExport.txt";
	
	public static void main(String[] args) {
		try {
//			Excel et = Excel.readFromResource("SDI-Sample.xls");
//			et.writeFile("C:\\Users\\User\\Desktop\\test\\SDI-Sample.xls");
//			Excel et = Excel.copyFromResource("SDI-Sample.xls","C:\\Users\\User\\Desktop\\test\\SDI-Sample.xls");
			TeradataSqlModelWrapper tw = new TeradataSqlModelWrapper();
			tw.createTable(getSQL());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getSQL() {
		return "\r\n"
				+ "CREATE SET TABLE PTEMP.PBOA_TO_WEBSC ,NO FALLBACK ,\r\n"
				+ "     NO BEFORE JOURNAL,\r\n"
				+ "     NO AFTER JOURNAL,\r\n"
				+ "     CHECKSUM = DEFAULT,\r\n"
				+ "     DEFAULT MERGEBLOCKRATIO\r\n"
				+ "     (\r\n"
				+ "      TIME_ID INTEGER,\r\n"
				+ "      OSTORE_ID INTEGER,\r\n"
				+ "      PRD_ID VARCHAR(7) CHARACTER SET LATIN NOT CASESPECIFIC NOT NULL,\r\n"
				+ "      FG_AUTO3 SMALLINT, \r\n"
				+ "      FORECAST INTEGER,\r\n"
				+ "      ML_ORDER_CNT_ADJUSTED INTEGER,\r\n"
				+ "      QT_ORD INTEGER,\r\n"
				+ "      QT_ITEM INTEGER,\r\n"
				+ "      RA_STORE INTEGER,\r\n"
				+ "	  ADOPTED DECIMAL(12,2),\r\n"
				+ "	  TOTAL_PRD INTEGER\r\n"
				+ "	  )\r\n"
				+ "UNIQUE PRIMARY INDEX ( TIME_ID ,OSTORE_ID ,PRD_ID ,FG_AUTO3 );\r\n"
				+ "";
	}
}
