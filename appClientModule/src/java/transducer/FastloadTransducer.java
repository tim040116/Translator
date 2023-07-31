package src.java.transducer;

import java.io.IOException;

import etec.common.utils.FileTool;
import src.java.params.BasicParams;

public class FastloadTransducer {
	public static String run(String fn,String fc) throws IOException {
		String result = "Success";
//		CreateListService.createFastloadLst(fn,fc);
		
		//整理SQL匯出檔案
		createSQLFile(fn, fc);
		
		return result;
	}
	//SQL轉換產檔
	private static String createSQLFile(String fn, String fc) throws IOException {
		System.out.println("createSQLFile(fastload)");
		String result = "Success";
		
		//清除註解
		String content = "\r\nSET NOCOUNT ON;\r\n\r\nBEGIN TRY\r\n";
		String strCatch = "\r\nEND TRY\r\n" + 
				"\r\n" + 
				"BEGIN CATCH\r\n" + 
				"  SELECT\r\n" + 
				"    ERROR_NUMBER() AS ErrorNumber,\r\n" + 
				"    ERROR_STATE() AS ErrorState,\r\n" + 
				"    ERROR_SEVERITY() AS ErrorSeverity,\r\n" + 
				"    ERROR_PROCEDURE() AS ErrorProcedure,\r\n" + 
				"    ERROR_MESSAGE() AS ErrorMessage;\r\n" + 
				"	 RETURN ERROR_STATE();\r\n" + 
				"END CATCH;";
		String dterminator = ",";	
		String insTB = "";

		for (String line : fc.split("\r\n")) {
			
			//取分隔符號
			if (line.contains("SET RECORD")) {
				if (!line.contains("UNFORMATTED")) {
					dterminator = line.replace("VARTEXT", "").replace("DISPLAY_ERRORS NOSTOP", "").replace("SET RECORD", "").replace(" ", "").replace(";", "");
					dterminator = dterminator.trim().substring(1, dterminator.length()-1);
				}
			}
			
			//target table
			if (line.contains("INSERT INTO")) {
				insTB = line.replace("INSERT INTO", "").replace("(", "");
			}

		}
		content =content +"COPY INTO "+insTB+"\r\n" + 
				"FROM 'https://ddimdls.dfs.core.windows.net/ddimfiles/Upload/Files/${datafile}'\r\n" + 
				"WITH (\r\n" + 
				"    CREDENTIAL=(IDENTITY= 'Shared Access Signature', SECRET='?sv=2020-08-04&ss=b&srt=co&sp=rlax&se=2022-03-31T09:57:24Z&st=2022-01-13T01:57:24Z&spr=https&sig=XHy83SNgvWHKDs%2BGr4U2xC9w5CTkjyr0fx0XVJHynf4%3D'),\r\n" + 
				"    FILE_TYPE = 'CSV',\r\n" + 
				"    FIELDQUOTE = '\"',\r\n" + 
				"    FIELDTERMINATOR='"+dterminator+"',\r\n" + 
				"    ENCODING = 'UTF8'\r\n" + 
				")\r\n" + 
				";";
		content = content + strCatch;
		//產檔
		String file = BasicParams.getTargetFileNm(fn);
		String[] arfn = file.split("\\\\");
		String frn = arfn[arfn.length-1];
		
		//產出檔名調整
		String fnn =//"fl_"+ 
		frn.replace(".pl", ".sql").replace(".btq", ".sql");
		file = file.replace(frn, fnn);
		FileTool.createFile(file,content);
		return result;
	}
	
}
