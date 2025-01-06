package etec.src.assignment.project.prg_scan.controller;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import etec.framework.code.interfaces.Controller;
import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.src.assignment.project.prg_scan.service.ScanExportService;
import etec.src.assignment.project.prg_scan.service.ScanFileListService;
import etec.src.assignment.project.prg_scan.service.ScanSPService;
import etec.src.assignment.project.prg_scan.service.ScanSrcTrgTableService;

/**
 * 對應新版UI
 *
 * @author Tim
 * @since 6.0.1.0
 **/
public class ProjectScanController implements Controller {

	private static SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

	JTextArea txtLog;

	JProgressBar progressBar;

	JPanel pnlStatusColor;

	long pgbTotal;
	long pgbLen;

	@Override
	public void run(Map<String, Object> args) {
		try {
			String inputPath = (String) args.get("inputPath");
			String outputPath = (String) args.get("outputPath") + "\\"	+ (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()) + "\\doc\\scan result.xlsxs";
			txtLog = (JTextArea) args.get("txtLog");
			pnlStatusColor = (JPanel) args.get("pnlStatusColor");
			progressBar = (JProgressBar) args.get("progressBar");

			// 儲存參數
			reset();
			addLog("資訊", "開始分析");
			pnlStatusColor.setBackground(Color.ORANGE);
			addLog("資訊", "取得資料目錄 : " + inputPath);
			addLog("資訊", "取得產檔目錄 : " + outputPath);
			// 取得檔案清單
			List<File> lf = null;
			lf = FileTool.getFileList(inputPath);
			addLog("資訊", "取得檔案清單");
			pgbTotal = lf.size();
			try(
					FileOutputStream fo = new FileOutputStream(outputPath);
					SXSSFWorkbook wb = new SXSSFWorkbook();
			){
				//載入service
				ScanFileListService fls = new ScanFileListService(wb);
				ScanSrcTrgTableService stts = new ScanSrcTrgTableService(wb);
				ScanSPService sps = new ScanSPService(wb);
				ScanExportService es = new ScanExportService(wb);
				//讀檔
				for (File f : lf) {
					// 讀檔案
					addLog("資訊", "讀取檔案：" + f.getPath());
					String content = FileTool.readFile(f);
					fls.addFileList(inputPath,f);
					// 寫入檔案清單
					content = ConvertRemarkSafely.cleanRemark(content);
					content = content
							.replaceAll("(?i)\\Q${DATA}\\E", "PDATA")
							.replaceAll("(?i)\\Q${MART}\\E", "PMART")
							.replaceAll("(?i)\\Q${TEMP}\\E", "PTEMP")
							.replaceAll("(?i)\\Q${STAGE}\\E", "PSTAGE");
					
					stts.getSrcTrg(f.getPath().replace(inputPath, ""), content);
					sps.getSP(f.getPath().replace(inputPath, ""), content);
					es.getSrcTrg(f.getPath().replace(inputPath, ""), content);
					//
					addLog("資訊", "讀取檔案：" + f.getPath());
					wb.write(fo);
					plusProgress();
				}
			}
			
			addLog("資訊", "分析成功");
			pnlStatusColor.setBackground(Color.GREEN);
		} catch (Exception e) {
			addLog("錯誤", "錯誤：" + e.getLocalizedMessage());
			pnlStatusColor.setBackground(Color.RED);
			Log.error(e);
			e.printStackTrace();
		}
	}
	
	public void  plusProgress(){
		pgbLen++;
		float val = (pgbLen*100/pgbTotal);
		progressBar.setValue((int)val);
		progressBar.setString((int)val+" %");
	}
	
	public void reset() {
		txtLog.setText("");
		pgbLen = 0;
		progressBar.setValue(0);
		progressBar.setString("0 %");
		pnlStatusColor.setBackground(Color.BLACK);
	}

	public void addLog(String level, String content) {
		Log.info(content);
		txtLog.append(sfabs.format(new Date()) + " [" + level.toUpperCase() + "] " + content + "\r\n");
	}

	@Override
	public Map<String, Object> getArgs() {
		// TODO Auto-generated method stub
		return null;
	}
}
