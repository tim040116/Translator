package etec.src.sql.az.service;

import java.io.IOException;
import java.util.List;

import etec.common.utils.FileTool;
import etec.common.utils.RegexTool;
import etec.src.file.model.BasicParams;
import etec.src.sql.az.translater.DDLTranslater;
import etec.src.sql.az.translater.DQLTranslater;

public class TransformService {
	public static String run(String fn, String fc) throws IOException {
		String result = "Success";
		//取出SQL段落
		String sql = getTransformSQL(fc);
		
		sql=sql
		.replaceAll(RegexTool.getReg("Create MultiSet Table"),"Create Table")
		.replaceAll(RegexTool.getReg("Create Set Table"),"Create Table")
		;
		
		//取出清單
		CreateListService.createQualifyRank(fn,sql);
		CreateListService.createODBC(fn,fc);
		CreateListService.createWithData(fn, sql);
		
		//整理SQL匯出檔案
		createSQLFile(fn, sql);
		return result;
	}
	
	//SQL轉換產檔
	private static String createSQLFile(String fn, String fc) throws IOException {
		System.out.println("createSQLFile");
		String result = "Success";
		String strTry = "\r\nSET NOCOUNT ON;\r\n\r\n";
		//清除註解
		String content = fc ;
		
		/* 22-06-13 新增 create set table 更新為 create table 
		 *         remove .replaceAll("[Cc][Aa][Ss][Tt] *\\(|[Aa][Ss] *[Dd][Aa][Tt][Ee] *[Ff][Oo][Rr][Mm][Aa][Tt] *'[YyMmDdHhSs-]*'\\)","")
		 * 23-10-03 Tim 將部分功能移轉到easyreplase 
		 * */
		//content = TransduceTool.changeAddMonth(content)
		content = content
				.replaceAll("[Ww][Ii][Tt][Hh] *[Cc][Oo][Uu][Nn][Tt]\\(\\*\\) *[Bb][Yy] *\\w*", "")
				.replaceAll(RegexTool.getReg("[Dd][Aa][Tt][Ee] [Ff][Oo][Rr][Mm][Aa][Tt] '[YyMmDdHhSs/\\-]*'"), "DATE")
				.replaceAll(RegexTool.getReg(" +[Dd][Aa][Tt][Ee] +'"), " '")
				.replaceAll(RegexTool.getReg("length \\("), "LEN(")//all
				.replaceAll(RegexTool.getReg("Character \\("), "LEN(")//all
				.replaceAll(RegexTool.getReg(" MINUS "), " EXCEPT ")//all


//				.replaceAll(RegexTool.getReg("(?<!_|[A-Za-z0-9])[Rr][Aa][Nn][Kk]\\((?! |\\))"), " RANK ( ) OVER ( order by ")//all
//				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Dd][Aa][Yy] *[Ff][Rr][Oo][Mm]", "DatePart(day ,")//all
//				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Mm][Oo][Nn][Tt][Hh] *[Ff][Rr][Oo][Mm]", "DatePart(month ,")//all
//				.replaceAll("[Ee][Xx][Tt][Rr][Aa][Cc][Tt] *\\( *[Yy][Ee][Aa][Rr] *[Ff][Rr][Oo][Mm]", "DatePart(year ,")//all
				;
		content = easyRemove(content);
		content = transformSingleSQL(content,fn);
		//20220613 	//content = TransduceTool.transduceSelectSQLTransduce(content);
		
		String tssql = DQLTranslater.transduceSelectSQL(content);
		content = strTry;
		int cntarea = 1;
		for(String div : tssql.split(";")) {
			String newDiv = addTryCatch(div,cntarea);
			content+=newDiv;
			cntarea++;
		}
		//產檔
		String file = BasicParams.getTargetFileNm(fn);
		String[] arfn = file.split("\\\\");
		String frn = arfn[arfn.length-1];
		
		//產出檔名調整
		String fnn = frn.replace(".pl", ".sql").replace(".btq", ".sql");
		file = file.replace(frn, fnn);
		FileTool.createFile(file,content);
		return result;
	}
	
	private static String addTryCatch(String sql,int areaCnt) {
		if("".equals(sql.trim())) {
			return "";
		}
		if(sql.trim().matches("ALTER\\s+VIEW[^;]+")) {
			return sql.trim()+"\r\n;\r\n";
		}
		String res = "\r\nBEGIN TRY\r\n\r\n";
		res += sql.trim() + "\r\n;\r\n\r\n";
		res += "END TRY\r\n" 
				+ "BEGIN CATCH\r\n" 
				+ "  SELECT '" + areaCnt + "' AS ErrorArea,\r\n"
				+ "    ERROR_NUMBER() AS ErrorNumber,\r\n" 
				+ "    ERROR_STATE() AS ErrorState,\r\n"
				+ "    ERROR_SEVERITY() AS ErrorSeverity,\r\n" 
				+ "    ERROR_PROCEDURE() AS ErrorProcedure,\r\n"
				+ "    ERROR_MESSAGE() AS ErrorMessage;\r\n" 
				+ "  RETURN ERROR_STATE();\r\n" 
				+ "END CATCH;\r\n";
		return res;
		
	}
	//取得sql的部分
	private static String getTransformSQL(String fc) {
		String res = "";
		boolean flag = false;
		for(String line : fc.split("\r\n") ) {
			if(line.matches(RegexTool.getReg(" \\/\\* End of bteq script \\*\\/ "))||
			   //line.matches(RegexTool.getReg(" \\/\\* End of bteq script \\*\\/ "))||
			   line.matches(RegexTool.getReg("[^;]*LOGOFF[^;]*;"))) {
				flag = false;
				break;
			}
			if(flag) {
				res += line+"\r\n";
			}
			if(line.matches(RegexTool.getReg(" \\/\\* Add you bteq options here \\*\\/  "))||
			   // line.matches(RegexTool.getReg(" \\/\\* Add you bteq options here \\*\\/  "))||
			   line.matches(RegexTool.getReg("[^;]*LOGON[^;]*;"))) {
				flag = true;
			}
			
		}
		return res;
	}
	/*
	 * 拆分每一段語法並處裡
	 * 
	 * 並依照種類進行處理
	 * */
	private static String transformSingleSQL(String fc,String fn) throws IOException {
		String res = "";
		String newSql = "";
		/*20220615  針對有含 like '%;%'的分號字元，先將%;%置換成TtEeSsTt 再處理
		 *         List<String> lstSql = RegexTool.getRegexTarget("(?!=;)[^;]*;",fc); 
		 *       =>List<String> lstSql = RegexTool.getRegexTarget("(?!=;)[^;]*;",fc.replaceAll("%;%","TtEeSsTt"));
		 * */
		List<String> lstSql = RegexTool.getRegexTarget("(?!=;)[^;]*;",fc.toUpperCase());
		for(String sql : lstSql) {
			sql = sql.trim();
//			//drop table
//			if(sql.matches("[^;]*"+RegexTool.getReg("drop table")+"[^;]*;")) {
//				//lst
//				//CreateListService.createLstDropTable(fn, sql);
//				//drop table
//				int sqlindex=sql.replaceAll("[Dd][Rr][Oo][Pp]","drop").indexOf("drop");
//				String BFsql="";
//				if( sqlindex > 0) {
//					BFsql=sql.substring(0,sqlindex);
//					sql=sql.substring(sqlindex);
//				}
//				String tableNm = sql.replaceAll("( *[Dd][Rr][Oo][Pp] *[Tt][Aa][Bb][Ll][Ee] *)|;|( *--\\S* *)","").trim();
//				newSql = BFsql+"IF OBJECT_ID(N'"+tableNm+"') IS NOT NULL \r\n"
//				+"DROP TABLE "+tableNm+" ; \r\n";
//				res += newSql+"\r\n";
//			}
			// drop table
			if (sql.matches("DROP\\s+TABLE\\s+\\S+\\s*;")) {
				res += DDLTranslater.runDropTable(sql)+"\r\n";
			}
			//drop view
			else if(sql.matches("[^;]*"+RegexTool.getReg("drop view")+"[^;]*;")) {
				//drop view
				int sqlindex=sql.replaceAll("[Dd][Rr][Oo][Pp]","drop").indexOf("drop");
				String BFsql="";
				if( sqlindex > 0) {
					BFsql=sql.substring(0,sqlindex);
					sql=sql.substring(sqlindex);
				}
				String tableVw = sql.replaceAll("( *[Dd][Rr][Oo][Pp] *[Vv][Ii][Ee][Ww] *)|;|( *--\\S* *)","").trim();
				newSql = BFsql+"IF OBJECT_ID(N'"+tableVw+"') IS NOT NULL \r\n"
				+"DROP VIEW "+tableVw+" ; \r\n";
				res += newSql+"\r\n";
			}
			//rename table
			else if(sql.matches("[^;]*"+RegexTool.getReg("rename table ")+"[^;]*;")) {
				//lst
				CreateListService.createLstRenameTable(fn, sql);
				//rename table
				int sqlindex=sql.replaceAll("[Rr][Ee][Nn][Aa][Mm][Ee]","rename").indexOf("rename");
				String BFsql="";
				if( sqlindex > 0) {
					//取出 rename 前面的字串
					BFsql=sql.substring(0,sqlindex);
					sql=sql.substring(sqlindex);
				}
				String[] tableNm = sql
						.replaceAll(RegexTool.getReg(",|rename table"), "")
						.split(" +[Tt][Oo] +");
				newSql = BFsql + " rename object "+tableNm[0].replaceAll(" ", "").trim()+" to "+tableNm[1].replaceAll(RegexTool.getReg("\\$\\{[^\\.]*\\."),"").replaceAll(";", "").trim()+"; \r\n\r\n";

				res += newSql+"\r\n";
			}
			//REPLACE VIEW
			else if(sql.matches("[^;]*"+RegexTool.getReg("REPLACE VIEW")+" [^;]*;")) {
				newSql = sql.replaceAll(RegexTool.getReg("REPLACE VIEW"), "ALTER VIEW");
				res += newSql+"\r\n";
			}
			//COLLECT STATISTICS
			else if(sql.matches("[^;]*"+RegexTool.getReg("COLLECT STATISTICS ")+"[^;]*;")) {
				newSql = sql.replaceAll(RegexTool.getReg("COLLECT STATISTICS ON"), "UPDATE STATISTICS");
				res += newSql+"\r\n";
			}
			//COMMENT ON
			else if(sql.matches("[^;]*"+RegexTool.getReg("COMMENT ON ")+"[^;]*;")) {
				newSql = "";
				res += newSql+"\r\n";
			}
			//insert into 
			else if(sql.matches("[^;]*"+RegexTool.getReg("Insert into")+" [^;]*;")) {
				newSql = sql;
				List<String> lstSelect = RegexTool.getRegexTarget(RegexTool.getReg("select"), newSql);
				if(!lstSelect.isEmpty()) {
					newSql = DQLTranslater.transduceSelectSQL(newSql);
				}
				res += newSql+"\r\n";
			}
			// CTAS
			else if (sql.matches("CREATE(\\s+VOLATILE)?\\s+TABLE\\s+\\S+\\s+AS\\s*\\([^;]+;")) {
				res += DDLTranslater.runCTAS(sql.replace(";",""))+"\r\n";
			}
			// create table no select
			//必須放在CTAS後面
			else if (sql.matches("CREATE\\s*(MULTISET|SET)?\\s+TABLE\\s+\\S+\\s+[^;]+;")) {
				res += DDLTranslater.runCreateTable(sql.replace(";",""))+"\r\n";
			}
//			//create table
//			//舊版
//			else if(sql.matches("[^;]*"+RegexTool.getReg("create table ")+"[^;]*;")) {
//				String tblNm = sql
//						.replaceAll(RegexTool.getReg(".* create table"), "")
//						.trim()
//						.replaceAll("\\s[^;]+;", "");
//				/*20220615  針對有含 like '%;%'的分號字元，先將%;%置換成TtEeSsTt 再處理
//				 *         String strCre=sql; =>String strCre=sql.replaceAll("TtEeSsTt","%;%"); 
//				 * */
//				String strCre=sql.replaceAll("TtEeSsTt","%;%");
////				strCre = TransduceService.transduceCreateSQL(strCre);
//				//create table
//				List<String> lstIndex = RegexTool.getRegexTarget2(RegexTool.getReg("(unique )?(primary)? index \\([^\\)]*\\)"), strCre);
//				for(String data : lstIndex) {
//					if(data!=null) {
//						//CreateListService.createIndexLst(tblNm,data,fn);
//						String strrpl = data.replaceAll("\\(", "\\\\\\(").replaceAll("\\)", "\\\\\\)");
//						// with語法
//						String withData = " with ( \r\n";
//						withData += "\tCLUSTERED COLUMNSTORE INDEX, \r\n";
//						List<String> lstCol = RegexTool.getRegexTarget("(?<=\\()[^\\)]+",data);
//						if(lstCol.isEmpty()) {
//							continue;
//						}
//						String[] arCol = lstCol.get(0).split(",");
//						String indexType = "";
//						if(arCol.length>=1) {
//							//indexType =" HASH( "+arCol[0]+" ) ";
//							indexType =" HASH( "+lstCol.get(0)+" ) ";
//						}else {
//							indexType =" REPLICATE ";
//						}
//						withData +="\tDISTRIBUTION = " + indexType + " \r\n ";
//						withData += ") ";
//						strCre = strCre.replaceAll(strrpl, withData);
//					}
//				}
//				List<String> lstSelect = RegexTool.getRegexTarget(RegexTool.getReg("select"), strCre);
//				for(int i = 0 ; i <= lstSelect.size();i++) {
//					if(!lstSelect.isEmpty()) {
//						strCre = DQLTransducer.transduceSelectSQL(strCre);
////						CreateListService.createCreateSelectLst(fn,sql);
//					}
//				}
//				strCre = strCre.replaceAll("[Ww][Ii][Tt][Hh] +[Dd][Aa][Tt][Aa]", "");
//				res += strCre + "\r\n\r\n";
//			}
			else {
				sql = DQLTranslater.changeSample(sql);
				sql = DQLTranslater.transduceSelectSQL(sql);
				res += sql + "\r\n\r\n";
			}
		}
		res=res.replaceAll("TtEeSsTt","%;%");
		return res;
	}
	/*
	 * 清除不需要的語法
	 * 
	 * */
	private static String easyRemove(String fc) {
		
        /* 20220613 Modify .replaceAll("COMPRESS", "") => .replaceAll("COMPRESS[^\\r\\n]*", "")
         * 20220613 add    .replaceAll(RegexTool.getReg("\\slogon \\$\\{USERID\\}, \\$\\{PASSWD\\};"),"")
         * 20220623 add    .replaceAll(RegexTool.getReg("\\.IF ACTIVITY")+"[^\\r\\n]*\\r\\n", "")
         * */
		
		String res = fc
			.replaceAll("\\.[Ss][Ee][Tt] [^\\r\\n]*\\r\\n", "")
			.replaceAll("\\.[Qq][Uu][Ii][Tt] *[0-9]*;", "")
			.replaceAll(RegexTool.getReg("\\.IF ERRORCODE <> 0 THEN")+"[^\\r\\n]*\\r\\n", "")
			.replaceAll(RegexTool.getReg("\\.SET ERROROUT STDOUT")+"[^\\r\\n]*\\r\\n", "")
			.replaceAll(RegexTool.getReg("\\.GOTO ERRORSFOUND")+"[^\\r\\n]*\\r\\n", "")
			.replaceAll(RegexTool.getReg("\\.LABEL ERRORSFOUND")+"[^\\r\\n]*\\r\\n", "")
			.replaceAll(RegexTool.getReg("\\.IF ACTIVITY")+"[^\\r\\n]*\\r\\n", "")
			.replaceAll("[^\\r\\n]*"+RegexTool.getReg("logon \\$\\{USERID\\}, \\$\\{PASSWD\\};"+"[^\\r\\n]*\\r\\n"),"")
			.replaceAll("[^\\r\\n]*"+RegexTool.getReg("LOGOFF;")+"[^\\r\\n]*\\r\\n", "")
			.replaceAll(RegexTool.getReg("\\.EXPORT RESET;"), "")
		;
		
		return res;
	}
//	// try catch
//		private static String addTryCatch(String sql) {
//			if ("".equals(sql.trim())) {
//				return "";
//			}
//			String res = "\r\nBEGIN TRY\r\n\r\n";
//			res += sql.trim() + "\r\n\r\n";
//			res += 	  "END TRY\r\n" 
//					+ "BEGIN CATCH\r\n" 
//					+ "  SELECT '" + cntarea + "' AS ErrorArea,\r\n"
//					+ "    ERROR_NUMBER() AS ErrorNumber,\r\n" 
//					+ "    ERROR_STATE() AS ErrorState,\r\n"
//					+ "    ERROR_SEVERITY() AS ErrorSeverity,\r\n" 
//					+ "    ERROR_PROCEDURE() AS ErrorProcedure,\r\n"
//					+ "    ERROR_MESSAGE() AS ErrorMessage;\r\n" 
//					+ "  RETURN ERROR_STATE();\r\n" 
//					+ "END CATCH;\r\n";
//			cntarea++;
//			return res;
//		}
}