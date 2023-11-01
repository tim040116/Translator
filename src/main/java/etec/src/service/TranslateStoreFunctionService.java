package etec.src.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import etec.common.exception.UnknowSQLTypeException;
import etec.common.model.BasicParams;
import etec.common.model.SFSPModel;
import etec.common.utils.FileTool;
import etec.common.utils.Log;
import etec.common.utils.RegexTool;
import etec.common.utils.TransduceTool;
import etec.main.Params;

/**
 * @author	Tim
 * @since	2023年10月17日
 * @version	3.3.1.0
 * 
 * 應Jason要求轉換store function
 * */
public class TranslateStoreFunctionService {

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
		String[] arrSF = content.toUpperCase().split("\"\\s*\"");
		//每一個sf的
		int i = 0;
		for(String sf : arrSF) {
			sf = TransduceTool.cleanRemark(sf.trim())
					.replaceAll("^\"", "")
					.replaceAll("\"\\+$", "");
			//取得檔名
			String sfName = RegexTool.getRegexTargetFirst("^\\s*\\S+\\s+FUNCTION\\s+[^\\(]+", sf)
					.replaceAll("^\\s*\\S+\\s+FUNCTION\\s+", "");
			String fileName = BasicParams.getTargetFileNm(sfName+".sql");
			//取得前段
			String txtFront = RegexTool.getRegexTargetFirst("^[\\S\\s]+\\s+RETURN\\s+", sf).trim()+"\r\n";
			//取得sql
			String txtSQL = RegexTool.getRegexTargetFirst("\\sRETURN\\s+[^;]+;", sf).replaceAll("\\s*RETURN\\s+", "").trim();
			//轉換sql
			txtSQL = TransduceTool.easyReplaceSelect(txtSQL);
			txtSQL = TransduceTool.changeAddMonth(txtSQL);
			txtSQL = TransduceTool.changeSample(txtSQL);
			txtSQL = TransduceTool.changeZeroifnull(txtSQL);
			txtSQL = TransduceTool.changeCharindex(txtSQL);
			txtSQL = TransduceTool.changeIndex(txtSQL);
			txtSQL = TransduceTool.easyReplaceCreate(txtSQL);
			//產檔
			String txt = txtFront+txtSQL;
			txt = txt.replaceAll("\n[\\t \r\n]+\n", "\r\n");
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
				.replaceAll("\"RequestText\"", "")
				.replaceAll("DECLARE EXIT HANDLER FOR SQLEXCEPTION\r\n" + 
						"BEGIN\r\n" + 
						"END;", "")
				;
		String sp = TransduceTool.cleanRemark(text.toUpperCase());
		String txtHeader = "";
		String txtContext = "";
		int step = 0;
		for(String line : sp.split("\r\n")) {
			if(line.matches("--.*")) {
				continue;
			}
			if(line.matches("\\s*(SP:)?BEGIN\\s*")) {
				step=1;
			}
			if(step==0) {
				txtHeader+=line+"\r\n";
			}else if(step==1){
				txtContext+=line+"\r\n";
			}
			
		}
		//取得檔名
		String spName = RegexTool.getRegexTargetFirst("^\\s*\\S+\\s+PROCEDURE\\s+[^\\(]+", sp)
				.replaceAll("^\\s*\\S+\\s+PROCEDURE\\s+", "");
		//轉換
		txtContext = transformSQL(txtContext);
		String script = txtHeader+txtContext;
		//header的參數
		List<String> lstParams = new ArrayList<String>();
		txtHeader = txtHeader
				.replaceAll(",", " ,")
				.replaceAll("IN\\s+(\\S+)", "$1")
				.replaceAll("IN\\s+(\\S+)", "$1");
		String headerParams = txtHeader
				.replaceAll("^[^\\(]+\\(","")
				.replaceAll("\\)\\s*SQL SECURITY INVOKER","")
				.replaceAll("\\([^\\)]+\\)", "")
				.replaceAll("([^,\\s]+)\\s+([^,\\s]+)", "$1")
				;
		lstParams.addAll(Arrays.asList(headerParams.split(",")));
		//DECLARE的參數
		lstParams.addAll(RegexTool.getRegexTarget("(?<=DECLARE )\\S+", txtContext));
		//參數置換
		if(Params.sfsp.TRANS_PARAMS) {
			//置換
			script = RegexTool.spaceRun(script, (String t) -> {
				for(String p: lstParams) {
					t = t.replaceAll("\\b"+p+"\\b", "@"+p);
				}
				return t;
			});
		}
		//包裝
		script = script.replaceAll("\n[\\t \r\n]+\n", "\r\n").trim();
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
		String sfName = RegexTool.getRegexTargetFirst("^\\s*\\S+\\s+FUNCTION\\s+[^\\(]+", sf)
				.replaceAll("^\\s*\\S+\\s+FUNCTION\\s+", "");
		//取得前段
		String txtHeader = RegexTool.getRegexTargetFirst("^[\\S\\s]+\\s+RETURN\\s+", sf).trim()+"\r\n";
		//取得sql
		String txtSQL = RegexTool.getRegexTargetFirst("\\sRETURN\\s+[^;]+;", sf).replaceAll("\\s*RETURN\\s+", "").trim();
		//轉換
		txtSQL = transformSQL(txtSQL);
		String script = txtHeader+txtSQL;
		//header的參數
		List<String> lstParams = new ArrayList<String>();
		String headerParams = txtHeader
				.replaceAll("^[^\\(]+\\(","")
				.replaceAll("\\)\\s*RETURNS",TransduceTool.SPLIT_CHAR_RED)
				.replaceAll(TransduceTool.SPLIT_CHAR_RED+"[\\S\\s]+", "")
				.replaceAll("\\([^\\)]+\\)", "")
				.replaceAll("([^,\\s]+)\\s+([^,\\s]+)", "$1")
				;
		lstParams.addAll(Arrays.asList(headerParams.split(",")));
		//DECLARE的參數
		lstParams.addAll(RegexTool.getRegexTarget("(?<=DECLARE )\\S+", txtSQL));
		//參數置換
		if(Params.sfsp.TRANS_PARAMS) {
			//置換
			script = RegexTool.spaceRun(script, (String t) -> {
				for(String p: lstParams) {
					t = t.replaceAll("\\b"+p+"\\b", "@"+p);
				}
				return t;
			});
		}
		//整理語法
		script = script.replaceAll("\n[\\t \r\n]+\n", "\r\n");
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
		txtSQL = TransduceTool.easyReplaceSelect(txtSQL);
		txtSQL = TransduceTool.changeAddMonth(txtSQL);
		txtSQL = TransduceTool.changeSample(txtSQL);
		txtSQL = TransduceTool.changeZeroifnull(txtSQL);
		txtSQL = TransduceTool.changeCharindex(txtSQL);
		txtSQL = TransduceTool.changeIndex(txtSQL);
		txtSQL = TransduceTool.easyReplaceCreate(txtSQL);
		return txtSQL;
//		return FamilyMartFileTransduceService.transduceSQLScript(sql);
	}
}
