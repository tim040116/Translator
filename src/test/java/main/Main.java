package main;

import java.io.File;
import java.util.Scanner;

import etec.common.utils.file.BigFileSplitTool;
import etec.common.utils.file.CompressTool;
import etec.src.sql.gp.translater.GreemPlumTranslater;
/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";
	
	public static void main(String[] args) {
		try {
//			BigFileSplitTool.splitFile(folder);
			Scanner sc = new Scanner(System.in);
			System.out.println("請輸入檔案路徑：");
			String zipPath = sc.next();
			BigFileSplitTool.concatFile(zipPath);
			System.out.println("完成");
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String str() {
		String str = "SELECT\r\n" + 
				"         CARD_NO\r\n" + 
				"        ,STORE_ID FAVOR_STORE_ID  --最喜歡消費的店\r\n" + 
				"       FROM SCV_PMART.SCV_ALL_ITEM_TP11\r\n" + 
				"       QUALIFY ROW_NUMBER() OVER (PARTITION BY CARD_NO\r\n" + 
				"               ORDER BY STORE_TXN_CNT DESC\r\n" + 
				"                       ,STORE_TXN_AMT DESC)=1";
		return str;
	}
}
