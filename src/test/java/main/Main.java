package main;

import java.text.SimpleDateFormat;
import java.util.Date;

import etec.common.utils.convert_safely.ConvertRemarkSafely;
import etec.src.sql.gp.translater.GreemPlumTranslater;
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
			String now = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
			
			String a = "SELECT \r\n" + 
					"	month,\r\n" + 
					"    monthly_sales,\r\n" + 
					"    monthly_expense \r\n" + 
					"from UNPIVOT(\r\n" + 
					"        ON( select * from T)\r\n" + 
					"        USING\r\n" + 
					"            VALUE_COLUMNS('monthly_sales', 'monthly_expense')\r\n" + 
					"            UNPIVOT_COLUMN('month')\r\n" + 
					"            COLUMN_LIST('jan_sales, jan_expense', 'feb_sales,feb_expense', 'mch_sales,mch_expense', 'apr_sales,apr_expense', 'may_sales,may_expense', 'jun_sales,jun_expense', 'jly_sales,jly_expense', 'ogs_sales,ogs_expense', 'sep_sales,sep_expense', 'oct_sales,oct_expense', 'nov_sales,nov_expense', 'dec_sales, dec_expense')\r\n" + 
					"            COLUMN_ALIAS_LIST('jan', 'feb', 'mch', 'apr', 'may', 'jun', 'jly', 'ogs', 'sep', 'oct', 'nov', 'dec' )\r\n" + 
					"    )X;";
			String b = GreemPlumTranslater.dql.changeUNPIVOT(a);
			System.out.println(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
