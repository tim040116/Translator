package main;

import etec.common.utils.excel.Excel;
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
			GreemPlumTranslater.dql.changeAliasName("");
			
//			Excel et = Excel.readFromResource("SDI-Sample.xls");
//			et.writeFile("C:\\Users\\User\\Desktop\\test\\SDI-Sample.xls");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
