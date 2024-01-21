 package etec.src.sql.az.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import etec.common.enums.SQLTypeEnum;
import etec.common.exception.UnknowSQLTypeException;
import etec.common.utils.FileTool;
import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;
import etec.common.utils.log.Log;
import etec.common.utils.param.Params;
import etec.src.file.model.BasicParams;
import etec.src.sql.az.translater.DDLTranslater;
import etec.src.sql.az.translater.DMLTranslater;
import etec.src.sql.az.translater.DQLTranslater;
import etec.src.sql.az.translater.OtherTranslater;
import etec.src.sql.td.classifier.TeradataClassifier;

/**
 * @author	Tim
 * @since	2023年10月4日
 * @version	3.3.0.0
 * 
 * 全家的城市轉換
 * */
public class FamilyMartFileTransduceService {

	/**
	 * @author	Tim
	 * @since	2023年10月3日
	 * 
	 * 依照檔案的種類執行對應的邏輯
	 * 
	 * */
	public static String run(File f) throws IOException {
		Log.info("FileTransduceService");
		String result = "SUCCESS";
		//副檔名
		String[] arrFileType = f.getName().split("\\.");
		String fileType = arrFileType[arrFileType.length-1].toLowerCase();
		String newFileName = BasicParams.getTargetFileNm(f.getPath());
		String newFileText = "";
		//取得內文
		//String charsetName = Params.familyMart.LIST_FILE_TYPE_BIG5.contains(fileType)?"big5":"utf-8";
		String charsetName = "utf-8";
		String ofc = FileTool.readFile(f,Charset.forName(charsetName))
				.replaceAll("\uFEFF", "").toUpperCase();
		newFileText = ofc.replaceAll("\\(", " ( ")
				.replaceAll("\\)", " ) ")
				;
		//轉換
		try {
			
			if("bat".equals(fileType)) {
				return "SKIP";
			}else if("txt".equals(fileType)) {
				return "SKIP";
			}else if("btq".equals(fileType)) {
				newFileText = transduceFileBTQ(newFileText);
				newFileName = newFileName.replaceAll("\\.[Bb][Tt][Qq]", ".sql");
			}else if("pl".equals(fileType)) {
				newFileText = transduceFilePL(newFileText);
				newFileName = newFileName.replaceAll("\\.[Pp][Ll]", ".sql");
			}else if("sql".equals(fileType)) {
				/*	2023/10/20	Tim
				 * .sql的檔案裏面是oracle所以不用轉換
				 * */
//				newFileText = transduceFileSQL(ofc);
				return "SKIP";
			}else if("fld".equals(fileType)) {
				return "SKIP";
			}else if("tpt".equals(fileType)) {
				return "SKIP";
			}
		}catch (UnknowSQLTypeException e) {
			//無法轉換的檔案
			newFileName = newFileName.replaceAll("\\\\src", "\\err");
			newFileText = FileTool.readFile(f,Charset.forName(charsetName));
			result = "SKIP";
		}
		//產檔
		FileTool.createFile(newFileName, newFileText,Params.familyMart.WRITE_FILE_CHARSET);
		return result;
	}
	/**
	 * @author	Tim
	 * @throws IOException 
	 * @since	2023年9月25日
	 * 
	 * BTEQ檔 擷取sql段落
	 * 
	 * 以login logout語法作為切分點
	 * 	
	 * */
	public static String transduceFileBTQ(String content) throws UnknowSQLTypeException,IOException {
		String result = "";
		String temp = "";
		int issql = 0;//0:還沒進sql 1:進sql 2:出sql
		for(String line : content.split("\r\n")) {
			if(line.matches("\\s*\\.LOGON\\s.*")) {//進入sql
				issql = 1;
				continue;
			}else if(line.matches("\\s*\\.LOGOFF;\\s*")) {//離開sql
				issql = 2;
			}else if(line.matches("\\s*\\..*")) {//btq語法
				continue;
			}else if(issql!=1&&line.matches("--.*")) {//註解
				result+=line+"\r\n";
				continue;
			}
			
			if(issql==1) {
				temp+=line+"\r\n";
			}
			if(issql==2){
				result+=transduceFileSQL(temp)+"\r\n";
				temp="";
			}
		}
		result+=transduceFileSQL(temp)+"\r\n";
		temp="";
		return result;
	}
	/**
	 * @author	Tim
	 * @throws IOException 
	 * @since	2023年9月25日 
	 * 
	 * perl檔 擷取sql段落
	 * 
	 * 因為perl檔其實是中間塞bteq語法再塞sql
	 * 所以中間用btq的邏輯就好
	 * 只需要處理註解
	 * 	
	 * */
	public static String transduceFilePL(String content) throws UnknowSQLTypeException,IOException {
		String result = "";
		//帶入參數
		if(Params.familyMart.IS_REPLACE_PARAMS_IN_PERL) {
			List<String> lstparam = RegexTool.getRegexTarget("MY\\s+\\$\\S+\\s+\\=\\s+\\$ENV[^;]+;", content);
			for(String strlst : lstparam) {
				String[] arrparam = strlst.replaceAll("MY\\s+\\$","")
					.replaceAll("\\$ENV\\{", "")
					.replaceAll("\\}\\s*;", "")
					.replaceAll("\\s+", "")
					.split("\\=");
				content = content.replaceAll("\\$\\{"+arrparam[0]+"\\}","${"+arrparam[1]+"}");
			}
		}
		//處理btq
		String isbtq = "";
		String btq = "";
		for(String line : content.split("\r\n")) {
			if(line.matches("\\s*#.*")&&"".equals(isbtq)) {//註解
				result+=line
						.replaceAll("##", "--")
						.replaceAll("\\s*#", "-- ")+"\r\n";
				continue;
			}else if(line.matches("\\.LOGON\\s.*")) {//進入sql
				isbtq = "A";
			}else if(line.matches("\\.LOGOFF;\\s*")) {//離開sql
				isbtq = "B";
			}
			
			if("A".equals(isbtq)) {
				btq+=line+"\r\n";
			}else if("B".equals(isbtq)) {
				btq+=line+"\r\n";
				result+=transduceFileBTQ(btq)+"\r\n";
				isbtq = "";
			}
		}
		return result;
	}
	/**
	 * @author	Tim
	 * @throws IOException 
	 * @since	2023年9月25日
	 * 
	 * 	sql檔有排除錯誤訊息跟結束執行的語法
	 * 然後要處理註解
	 * 
	 * */
	public static String transduceFileSQL(String content) throws UnknowSQLTypeException,IOException {
		String result = "";
		String temp = "";
		boolean isremark = false;
		content = content//移除sql檔的特殊語法
				.replaceAll("WHENEVER\\s+SQLERROR\\s+EXIT\\s+FAILURE\\s+ROLLBACK", "")
				.replaceAll("WHENEVER\\s+OSERROR\\s+EXIT\\s+FAILURE\\s+ROLLBACK", "")
				.replaceAll("EXIT\\s+SQL\\.SQLCODE;", "")
				.replaceAll("\\sBEGIN\\s", "\r\n")
				.replaceAll("END\\s*;", "")
				;
		for(String line : content.split("\r\n")) {
			if(line.equals("/*")) {
				isremark = true;
			}
			if(line.equals("*/")) {
				isremark = false;
				result+=line+"\r\n";
				continue;
			}
			if(isremark) {
				result+=line+"\r\n";
			}else {
				if(line.matches("\\s*--.*")) {
					result+=line+"\r\n";
				}else {
					temp+=line+"\r\n";
					if(line.matches("[^;]*;\\s*")) {
						result+=transduceSQLScript(temp)+"\r\n";
						temp="";
					}
				}
			}
		}
		return result;
	}
	/**
	 * @author	Tim
	 * @since	2023年9月25日
	 * @param	String	單一斷落的SQL，記得要全大寫
	 * 轉換sql語句
	 * 因配合檔案轉換時不同語法的註解
	 * 只轉換一段sql指令
	 * @throws IOException 
	 * */
	public static String transduceSQLScript(String content)throws UnknowSQLTypeException , IOException {
		String result = "";
		result = content.toUpperCase().trim().replaceAll("\\bSEL\\b", "SELECT");
		//清除註解
		String cleanSQL = TransduceTool.cleanRemark(result);
		//區分類型
		SQLTypeEnum sqlType = TeradataClassifier.getSQLType(cleanSQL);
		Log.info("語法類別："+sqlType);
		switch(sqlType) {
			case TRUNCATE_TABLE:
				break;
			case REPLACE_VIEW:
				result = DDLTranslater.runReplaceView(result);
				break;
			case DROP_VIEW:
			case DROP_TABLE:
				result = DDLTranslater.runDropTable(result);
				break;
			case RENAME_TABLE:
				result = DDLTranslater.runRenameTable(result);
				break;
			case COLLECT_STATISTICS:
				result = DDLTranslater.runStatistics(result);
				break;
			case CREATE_TABLE:
				result = DDLTranslater.runCreateTable(result);
				break;
			case CREATE_INSERT:
				result = DDLTranslater.runCTAS(result);
				break;
			case CTAS:
				break;
			case SELECT_INTO:
				result = DDLTranslater.runSelectIntoToCTAS(result);
				break;
			case INSERT_TABLE:
				result = DMLTranslater.runInsertInto(result);
				break;
			case INSERT_SELECT:
				result = DMLTranslater.runInsertSelect(result);
				break;
			case MERGE_INTO:
				result = DMLTranslater.runMergeInto(result);
				break;
			case SELECT_TABLE:
				result = DQLTranslater.transduceSelectSQL(result);
				break;
			case UPDATE_TABLE:
				result = DQLTranslater.transduceSelectSQL(result);
				break;
			case DELETE_TABLE:
				result = DQLTranslater.transduceSelectSQL(result);
				break;
			case WITH:
				result = DQLTranslater.transduceSelectSQL(result);
				break;	
			case EMPTY:
				break;
			case DATABASE:
				break;
			case LOCKING:
				break;
			case CALL:
				result = OtherTranslater.transduceCall(result);
				break;
			case COMMENT_ON:
				break;
			case COMMIT:
				break;
			case BT:
				break;
			case ET:
				break;
			case EXIT:
				break;
			case OTHER:
				throw new UnknowSQLTypeException(result,sqlType);
			default:
				break;
		}
		result = result
				.replaceAll(" \\( ", "(")
				.replaceAll(" \\) ", ")")
				;
		return result;
	}
}
