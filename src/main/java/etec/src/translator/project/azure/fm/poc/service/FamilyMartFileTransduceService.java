 package etec.src.translator.project.azure.fm.poc.service;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.factory.TranslaterFactory;
import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.context.translater.exception.SQLTranslateException;
import etec.framework.file.readfile.service.CharsetTool;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.src.translator.project.azure.fm.formal.service.FmSqlService;
import etec.src.translator.sql.az.translater.AzTranslater;

/**
 * @author	Tim
 * @since	2023年10月4日
 * @version	3.3.0.0
 *
 * 全家的城市轉換
 * */
public class FamilyMartFileTransduceService {


	/**
	 * <h1>執行入口</h1>
	 * <br>抄AzureTranslateService
	 * <br>加上全家客製化功能
	 *
	 * <h2>異動紀錄</h2>
	 * <br>2024年09月02日	Tim	建立功能
	 * <br>2024年10月21日	Tim	將不同副檔名分開處理，主要是為了轉換fastload
	 * @author Tim
	 * @since 2024年9月2日
	 * @param context 檔案的內容
	 * @see
	 * @return
	 * @throws Exception 
	 */
	public static void run(String outputPath,File f) throws Exception {
		
		if(f.getName().toUpperCase().contains(".BTQ")) {
			Log.info("檔案類型：BTEQ");
			transduceBtq(outputPath, f);
		}else if(f.getName().toUpperCase().contains(".FLD")) {
			Log.info("檔案類型：FLD");
			transduceFld(outputPath, f);
		}else if (f.getName().toUpperCase().contains(".SQL")){
			Log.info("檔案類型：SQL");
			transduceSql(outputPath, f);
		}else {
			Log.warn("檔案類型：其他");
		}
	}
	
	
	public static void transduceSql(String outputPath,File f) throws Exception {
		Charset chs = CharsetTool.getCharset(f.getPath());
		String context = CharsetTool.readFileInCharset(chs.name(),f.getPath());
		
		Log.debug("清理註解");
		String newContext = ConvertRemarkSafely.savelyConvert(context, (t) -> {
			if(f.getName().toUpperCase().contains(".FLD")) {
				
			}
			t = FmSqlService.preReplace(t);
			StringBuffer sb = new StringBuffer();
			try {
				Log.debug("開始捕獲語法");
				String reg = "(#\\*s)?\\b(?:" + String.join("|",TranslaterFactory.getTitleList()) + ")\\b[^;]+?;";
				Matcher m = Pattern.compile(reg, Pattern.CASE_INSENSITIVE).matcher(t);
				while (m.find()) {
					// 處理前後空白
					String sql = m.group().trim();
					sql = FmSqlService.replaceAll(sql).toLowerCase();
					sql = AzTranslater.translate(sql);
					m.appendReplacement(sb, Matcher.quoteReplacement(sql + "\r\n"));
				}
				m.appendTail(sb);
			} catch (SQLTranslateException e) {
				e.printStackTrace();
			}
			return sb.toString();
		});

		//全家客製化項目
		FileTool.createFile(outputPath, newContext, chs);
	}
	
	public static void transduceFld(String outputPath,File f) throws Exception {
		/* 2024/05/06	Tim	強制轉換成指定編碼
		 * */
//		String context = FileTool.readFile(f);
		Charset chs = CharsetTool.getCharset(f.getPath());
		String context = CharsetTool.readFileInCharset(chs.name(),f.getPath());
		
		String res = "";
		context = context.replaceAll("(?i)(?:MULTI)?SET", "");
		String regsql = "(?i)CREATE\\s+TABLE\\s+([^\\s()]+)(?:\\s+AS)?\\s*\\(\\s*+([\\S\\s]+?)\\s*\\)[^;()]*;";
		Matcher msql = Pattern.compile(regsql).matcher(context);
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
				+ "IF OBJECT_ID('dev.stg_"+ tableNm +"_02','U') IS NOT NULL\r\n"
				+ "BEGIN\r\n"
				+ "DROP TABLE dev.stg_" + tableNm + "_02 ;\r\n"
				+ "END\r\n"
				+ "CREATE TABLE dev.stg_" + tableNm + "_02 \r\n"
				+ "WITH (\r\n"
				+ "\t HEAP\r\n"
				+ "\t,DISTRIBUTION = ROUND_ROBIN\r\n"
				+ ")\r\n"
				+ "AS\r\n"
				+ "SELECT\r\n"
				+ "\t " + String.join("\r\n\t,", lstCol) + "\r\n"
				+ "FROM stg_" + tableNm + "_01 ; \r\n"
				
			;
		}

		/*將bteq語法清除*/
		FileTool.createFile(outputPath, context, chs);
		FileTool.createFile(outputPath.replaceAll("(?i)\\.fld", ".fld.sql"), res, chs);
	}
	
	public static void transduceBtq(String outputPath,File f) throws Exception {
		/* 2024/05/06	Tim	強制轉換成指定編碼
		 * */
//		String context = FileTool.readFile(f);
		Charset chs = CharsetTool.getCharset(f.getPath());
		String context = CharsetTool.readFileInCharset(chs.name(),f.getPath());
		
		Log.debug("清理註解");
		String newContext = ConvertRemarkSafely.savelyConvert(context, (t) -> {
			if(f.getName().toUpperCase().contains(".FLD")) {
				
			}
			t = FmSqlService.preReplace(t);
			StringBuffer sb = new StringBuffer();
			try {
				Log.debug("開始捕獲語法");
				t = t
					//locking
					.replaceAll("(?i)\\bFOR\\s+ACCESS(?!\\s*;)", "$0;")
					//.
					.replaceAll("\\n\\s*\\..*", "$0;")
					.replaceAll(";\\s*;", ";")
				;
				/**
				 * <p>功能 ：捕獲SQL語法</p>
				 * <p>類型 ：搜尋</p>
				 * <p>修飾詞：gmi</p>
				 * <p>範圍 ：從 開頭 到 分號</p>
				 * <h2>群組 ：</h2>
				 * <h2>備註 ：</h2>
				 * 	會先將TranslaterService裡的各類型title用|串接，
				 *  作為開頭
				 * <h2>異動紀錄 ：</h2>
				 * 2024年4月10日	Tim	建立邏輯
				 * 2024年5月6日	Tim	增加所有類型的title
				 * */
				String reg = "(#\\*s)?\\b(?:" + String.join("|",TranslaterFactory.getTitleList()) + ")\\b[^;]+?;";
				Matcher m = Pattern.compile(reg, Pattern.CASE_INSENSITIVE).matcher(t);
				while (m.find()) {
					// 處理前後空白
					String sql = m.group().trim();
					sql = AzTranslater.translate(sql);
					m.appendReplacement(sb, Matcher.quoteReplacement(sql + "\r\n"));
				}
				m.appendTail(sb);
			} catch (SQLTranslateException e) {
				e.printStackTrace();
			}
			return sb.toString();
		});

		//全家客製化項目
		newContext = fmOnly(newContext);
		outputPath = outputPath.replaceAll("(?i)\\.btq.*", ".sql");
		/*將bteq語法清除*/
		//newContext = newContext.replaceAll("\\r\\n\\..*", "");
//		newContext = GreenPlumTranslater.dql.changeMultAnalyze(newContext);
		FileTool.createFile(outputPath, newContext, chs);
		
	}
	
	
	private static String fmOnly(String content) throws Exception{
		String res = content;
		res = FmSqlService.easyReplace(res);
		return res;
	}
}
