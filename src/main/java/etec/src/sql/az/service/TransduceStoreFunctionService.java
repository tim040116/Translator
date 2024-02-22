package etec.src.sql.az.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import etec.common.exception.sql.UnknowSQLTypeException;
import etec.common.utils.FileTool;
import etec.common.utils.Mark;
import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;
import etec.common.utils.log.Log;
import etec.src.file.model.BasicParams;
import etec.src.file.model.SFSPModel;
import etec.src.sql.az.translater.DDLTranslater;
import etec.src.sql.az.translater.DQLTranslater;
import etec.src.sql.az.translater.OtherTranslater;
import etec.src.sql.az.translater.SQLTranslater;

/**
 * @author	Tim
 * @since	2023年10月17日
 * @version	3.3.1.0
 * 
 * 應Jason要求轉換store function
 * */
public class TransduceStoreFunctionService {

	/**
	 * @author	Tim
	 * @since	2023年10月17日
	 * 
	 * */
	public static String run(File f) throws IOException {
		Log.info("TranslateStoreFunctionService");
		String result = "SUCCESS";
		//讀檔
		String content = FileTool.readFile(f);
		String[] arrSF = content
//				.toUpperCase()
				.split("\"\\s*\"");
		//每一個sf的
		int i = 0;
		for(String sf : arrSF) {
			/**
			 * @author	Tim
			 * @since	2023年11月20日
			 * 
			 * 應Joyce要求，SQL的註解不應該被去除
			 * */
			sf = sf.trim()/*TransduceTool.cleanRemark(sf.trim())*/
					.replaceAll("^\"", "")
					.replaceAll("\"\\+$", "");
			//取得檔名
			String sfName = RegexTool.getRegexTargetFirst("(?i)^\\s*\\S+\\s+FUNCTION\\s+[^\\(]+", sf)
					.replaceAll("(?i)^\\s*\\S+\\s+FUNCTION\\s+", "");
			String fileName = BasicParams.getTargetFileNm(sfName+".sql");
			//取得前段
			String txtFront = RegexTool.getRegexTargetFirst("(?i)^[\\S\\s]+\\s+RETURN\\s+", sf).trim()+"\r\n";
			//取得sql
			String txtSQL = RegexTool.getRegexTargetFirst("(?i)\\sRETURN\\s+[^;]+;", sf).replaceAll("(?i)\\s*RETURN\\s+", "").trim();
			//轉換sql
			txtSQL = DQLTranslater.easyReplaceSelect(txtSQL);
			txtSQL = DQLTranslater.changeAddMonth(txtSQL);
			txtSQL = TransduceTool.changeSample(txtSQL);
			txtSQL = DQLTranslater.changeZeroifnull(txtSQL);
//			txtSQL = TransduceTool.changeCharindex(txtSQL);
			txtSQL = OtherTranslater.changeIndex(txtSQL);
			txtSQL = TransduceTool.easyReplaceCreate(txtSQL);
			//產檔
			String txt = txtFront+txtSQL;
			txt = txt.replaceAll("(?mi)^\\s+$", "");
			FileTool.createFile(fileName,txt);
			i++;
		}
		Log.info("Success "+i);
		return result;
	}
	/**
	 * @author	Tim
	 * @throws IOException 
	 * @throws UnknowSQLTypeException 
	 * @since	2023年10月23日
	 * 		
	 * 
	 * */
	public static SFSPModel transformSP(File f) throws IOException, UnknowSQLTypeException{
		String text = FileTool.readFile(f,Charset.forName("UTF-8"));
		SFSPModel res = new SFSPModel();
		//初步處理
		text = text
				.replaceAll(" {2,3}", "\t")
				.replaceAll("(?i)\"RequestText\"", "")
				.replaceAll("(?i)DECLARE EXIT HANDLER FOR SQLEXCEPTION\r\n" + 
						"BEGIN\r\n" + 
						"END;", "")
				;
		/**
		 * @author	Tim
		 * @since	2023年11月20日
		 * 
		 * 應Joyce要求，SQL的註解不應該被去除
		 * */
//		String sp = TransduceTool.cleanRemark(text.toUpperCase());
		String sp = text
//				.toUpperCase()
				.trim();
		String txtHeader = "";
		String txtContext = "";
		int step = 0;
		for(String line : sp.split("\r\n")) {
			if(line.matches("(?i)\\s*(SP:)?BEGIN\\s*")) {
				step=1;
			}
			if(step==0) {
				if(line.matches("--.*")) {
					txtHeader+=line+"\r\n";
					continue;
				}
				txtHeader+=(line+"\r\n")
						.replaceAll("(?i)OUT\\s+(\\S+\\s+\\S+)", "$1 OUT ")
						.replaceAll("(?i), OUT", " OUT ,")
						.replaceAll("(?i)IN\\s+(\\S+)", "$1");
			}else if(step==1){
				txtContext+=line+"\r\n";
			}
			
		}
		//
		//取得檔名
		String spName = RegexTool.getRegexTargetFirst("(?i)^\\s*\\S+\\s+PROCEDURE\\s+[^\\(]+", TransduceTool.cleanRemark(sp))
				.replaceAll("(?i)^\\s*\\S+\\s+PROCEDURE\\s+", "");
		//轉換
		txtContext = transformSQL(txtContext);
		String script = txtHeader
				.replaceAll("(?i)(\\S+)\\s+PROCEDURE\\s+([^\\s\\.]+)\\.([^\\s\\.\\(]+)", "USE $2\r\nGO\r\n$1 PROCEDURE $3 ")
				+txtContext;
		//header的參數
		List<String> lstParams = new ArrayList<String>();
		txtHeader = TransduceTool.cleanRemark(txtHeader)
				.replaceAll("(?i)\\bCASESPECIFIC\\b", "")
				.replaceAll("(?i)CHARACTER(\\s+SET)?\\s+\\w+", "")
				.replaceAll("(?i)SQL\\s+SECURITY\\s+INVOKER", "")
				.replaceAll(",", " ,")
				.replaceAll("^[^\\(]+\\(","")
				.replaceAll("(?i)SP:","")
				.replaceAll("\\)\\s*$","")
				;
		String headerParams = txtHeader;
		headerParams = headerParams.replaceAll("--.*", "");
//		headerParams = headerParams.replaceAll("^[^\\(]+\\(","");
		headerParams = headerParams.replaceAll("(?i)\\)\\s*SQL SECURITY INVOKER","");
		headerParams = headerParams.replaceAll("\\([^\\)]+\\)", "");
		headerParams = headerParams.replaceAll("([^,\\s]+)\\s+([^,\\s]+)", "$1");
		headerParams = headerParams.replaceAll("([^,\\s]+)\\s+([^,]+)","$1");
				;
		lstParams.addAll(Arrays.asList(headerParams.split("\\s*,\\s*")));
		//DECLARE的參數
		lstParams.addAll(RegexTool.getRegexTarget("(?i)(?<=DECLARE\\s{0,100})\\S+", txtContext));
		//參數置換
		script = OtherTranslater.transduceDECLARE(lstParams, script);
		//包裝
		script = findSQLSTR(script);
		script = SQLTranslater.convertDecode(script);
		script = script
				.replaceAll("(?i)SQL\\s+SECURITY\\s+INVOKER", "")
				.replaceAll("(?i)\\bSP\\s*:\\s*BEGIN", "AS BEGIN")
				.replaceAll("(?i)\\bEND\\s+SP\\b", "END")
				.replaceAll("\n[\\t \r\n]+\n", "\r\n")
				.replaceAll("(?i)VARCHAR\\s*\\(\\s*([8-9][0-9]{3}|[0-9]{5,})\\s*\\)", "VARCHAR(MAX)")
				.trim();
		res.setName(spName);
		res.setHeader(txtHeader);
		res.setContext(txtContext);
		res.setScript(script);
//		res.put(spName.trim(), txt);
//		res.put(f.getName(), txt.trim());
		return res;
	}
	/**
	 * @author	Tim
	 * @throws IOException 
	 * @throws UnknowSQLTypeException 
	 * @since	2023年10月23日
	 * 		- 只有一個檔案以雙引號包住sql,沒有分隔符號
	 * 		- 只轉換 RETURN 到  ; 中間的語法,其餘直接搬
	 * 		- 註解全清掉
	 * 		- 每隻sf產一個檔,檔名為function name
	 * 
	 * */
	public static SFSPModel transformSF(String sf) throws IOException, UnknowSQLTypeException{
		SFSPModel res = new SFSPModel();
		//每一個sf的
		sf = TransduceTool.cleanRemark(sf.trim())
				.replaceAll("^\"", "")
				.replaceAll("\"\\+$", "");
		//取得檔名
		String sfName = RegexTool.getRegexTargetFirst("(?i)^\\s*\\S+\\s+FUNCTION\\s+[^\\(]+", sf)
				.replaceAll("(?i)^\\s*\\S+\\s+FUNCTION\\s+", "");
		//取得前段
		String txtHeader = RegexTool.getRegexTargetFirst("(?i)^[\\S\\s]+\\s+RETURN\\s+", sf).trim()+"\r\n";
		//取得sql
		String txtSQL = RegexTool.getRegexTargetFirst("(?i)\\sRETURN\\s+[^;]+;", sf).replaceAll("(?i)\\s*RETURN\\s+", "").trim();
		//轉換
		txtSQL = transformSQL(txtSQL);
		String script = txtHeader+txtSQL;
		//header的參數
		List<String> lstParams = new ArrayList<String>();
		String headerParams = txtHeader
				.replaceAll("(?i)\\bCASESPECIFIC\\b", "")
				.replaceAll("(?i)CHARACTER(\\s+SET)?\\s+\\w+", "")
				.replaceAll("(?i)SQL\\s+SECURITY\\s+INVOKER", "")
				.replaceAll("^[^\\(]+\\(","")
				.replaceAll("(?i)\\)\\s*RETURNS",Mark.MAHJONG_RED)
				.replaceAll(Mark.MAHJONG_RED+"[\\S\\s]+", "")
				.replaceAll("\\([^\\)]+\\)", "")
				.replaceAll("([^,\\s]+)\\s+([^,\\s]+)", "$1")
				;
		lstParams.addAll(Arrays.asList(headerParams.split("\\s*,\\s*")));
		//DECLARE的參數
		lstParams.addAll(RegexTool.getRegexTarget("(?i)(?<=DECLARE )\\S+", txtSQL));
		//參數置換
		script = OtherTranslater.transduceDECLARE(lstParams, script);
		//整理語法
		script = script
				.replaceAll("(?i)SQL\\s+SECURITY\\s+INVOKER", "")
				.replaceAll("(?i)\\bSP\\s*:\\s*BEGIN", "AS BEGIN")
				.replaceAll("(?i)\\bEND\\s+SP\\b", "END")
				.replaceAll("\n[\\t \r\n]+\n", "\r\n")
				;
		script = findSQLSTR(script);
		res.setHeader(txtHeader);
		res.setContext(txtSQL);
		res.setScript(script);
		res.setParams(lstParams);
		res.setName(sfName);
		res.setType("SF");
//		res.put(sfName, txt);
		return res;
	}
	/**
	 * @author	Tim
	 * @throws IOException 
	 * @throws UnknowSQLTypeException 
	 * @since	2023年10月23日
	 * 只處理sql的部分
	 * 
	 * */
	public static String transformSQL(String txtSQL) throws UnknowSQLTypeException, IOException {
		//轉換sql
		txtSQL = DQLTranslater.easyReplaceSelect(txtSQL);
		txtSQL = DQLTranslater.changeAddMonth(txtSQL);
		txtSQL = TransduceTool.changeSample(txtSQL);
		txtSQL = TransduceTool.changeCharindex(txtSQL);
		txtSQL = OtherTranslater.changeIndex(txtSQL);
		txtSQL = TransduceTool.easyReplaceCreate(txtSQL);
		txtSQL = OtherTranslater.transduceCursor(txtSQL);
		txtSQL = OtherTranslater.transduceIF(txtSQL);
		txtSQL = OtherTranslater.transduceCall(txtSQL);
		txtSQL = DDLTranslater.easyReplace(txtSQL);
		txtSQL = DQLTranslater.changeZeroifnull(txtSQL);
		//CURSOR
		//IF SQLSTATE <> '00000' THEN LEAVE L1; end if;
		txtSQL = txtSQL
				.replaceAll("(?i)\\bIF\\s+(\\w+)\\s+<\\s*>\\s+\\w+\\s+THEN\\s+LEAVE\\s+(\\w+)\\s*;\\s*END\\s+IF\\s*;"
						, "WHILE (@@FETCH_STATUS = 0)")
				.replaceAll("(?i)\\bEXEC(UTE)?\\s+([^;\\s]+)\\s*;", "EXECUTE sp_executesql $2;")
				;
		return txtSQL;
//		return FamilyMartFileTransduceService.transduceSQLScript(sql);
	}
	
	/*
	 * 處理 SET @SQLSTR = ''; 裡面的SQL雨具
	 * */
	public static String findSQLSTR(String script) {
		String res = "";
		String tmp = "";
		boolean isQuery = false;
		for(String line : script.split("\r\n")) {
			if(line.matches("(?i)\\s*SET\\s+@\\S+\\s*=\\s*")) {
				tmp = "";
				isQuery = true;
			}
			if(!isQuery) {
				res+=line+"\r\n";
				continue;
			}
			tmp+=line+"\r\n";
			if(line.matches("(?i).*'\\s*;\\s*")) {
				isQuery = false;
				String set = tmp.replaceAll("(?i)(\\s*SET\\s+\\S+\\s*=\\s*)[\\S\\s]+", "$1");
				String src = tmp.replaceAll("(?i)\\s*SET\\s+\\S+\\s*=\\s*", "");
				res+= set+openSQLSTR(src)+"\r\n";
			}
		}
		return res;
	}
	/**
	 * SQLSTR轉成SQL
	 * */
	public static String openSQLSTR(String script) {
		String res = "";
		
		res = script
			.replaceAll("\\s*;\\s*$", "")
			.replaceAll("(?<![\\+\\'])'(?![\\+\\'])", "")
			.replaceAll("'{2}?", "'")
			.replaceAll("(?m)'\\s*\\+\\s*$", "")
			.replaceAll("'\\s*$", "")
			.replaceAll("^\\s*\\+\\s*'", "")
		;
		
		res = SQLTranslater.convertDecode(res);
		
		res = res
			.replaceAll("'", "''")
			.replaceAll("'('\\s*\\+.*?\\s*\\+\\s*')'", "$1")
			.replaceAll("(?m)^", "'")
			.replaceAll("(?m)$", "' + ")
			.replaceAll("'\\s*\\+\\s*$", "';")
		;
		return res;
	}
}
