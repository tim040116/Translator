package etec.src.assignment.project.prg_scan.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import etec.framework.security.log.service.Log;

/**
 * <h1>產生分析結果</h1>
 * <p></p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年12月10日	Tim	建立功能
 * 
 * @author	Tim
 * @since	6.0.2.1
 * @see
 */
public class ScanSPService extends BasicScanService{
	

	
	public ScanSPService(SXSSFWorkbook wb) {
		super(wb,"SP",new String[] { "檔名","名稱","參數"});
	}

	public void getSP(String fileName, String content) throws FileNotFoundException, IOException {
		Log.info("開始分析 SP : " + fileName);
		String regs = "(?i)CALL\\s+(?<spName>"+getSchemaName()+"\\.\\w+)\\s*\\(\\s*(?<param>[^;]+)\\s*\\)\\s*;";// 到最底，為了組rerun
		Matcher ms = Pattern.compile(regs).matcher(content);
		while (ms.find()) {
			String spName = ms.group("spName").toUpperCase();
			String param = ms.group("param").trim();
			addRow(new String[] {fileName,spName,param});
		}
		Log.info("分析完成");
	}
	
}
