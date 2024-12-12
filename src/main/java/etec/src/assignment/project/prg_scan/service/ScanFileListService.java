package etec.src.assignment.project.prg_scan.service;

import java.io.File;
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
public class ScanFileListService extends BasicScanService{
	

	
	public ScanFileListService(SXSSFWorkbook wb) {
		super(wb,"檔案清單",new String[] { "路徑","檔名"});
	}

	public void addFileList(String rootDir,File f) throws FileNotFoundException, IOException {
		String fileName = f.getName();
		String dir = f.getParent().replace(rootDir,"");
		addRow(new String[] {dir,fileName});
	}
	
}
