package etec.src.translator.project.azure.fm.formal.controller;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import etec.common.factory.Params;
import etec.framework.code.interfaces.Controller;
import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.common.model.BasicParams;
import etec.src.translator.common.service.IOpathSettingService;
import etec.src.translator.project.azure.ddim.service.CreateListService;
import etec.src.translator.project.azure.fm.poc.service.FamilyMartFileTransduceService;
import etec.src.translator.view.panel.SearchFunctionPnl;

/**
 * 對應新版UI
 *
 * @author Tim
 * @since 6.0.1.0
 **/
public class FamilyMartController2 implements Controller {

	private static SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

	JTextArea txtLog;

	JProgressBar progressBar;

	JPanel pnlStatusColor;

	long pgbTotal;
	long pgbLen;

	@Override
	public void run(Map<String, Object> args) {
		try {
			String inputPath = (String) args.get("inputPath");
			String outputPaath = (String) args.get("outputPath") + "\\"	+ (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()) + "\\src\\";
			txtLog = (JTextArea) args.get("txtLog");
			pnlStatusColor = (JPanel) args.get("pnlStatusColor");
			progressBar = (JProgressBar) args.get("progressBar");

			// 儲存參數
			reset();
			addLog("資訊", "取得資料目錄 : " + inputPath);
			addLog("資訊", "取得產檔目錄 : " + outputPaath);
			// 取得檔案清單
			List<File> lf = null;
			lf = FileTool.getFileList(inputPath);
			addLog("資訊", "取得檔案清單");
			pgbTotal = lf.size();
			for (File f : lf) {
				// 讀檔案
				addLog("資訊", "讀取檔案：" + f.getPath());
				// 寫入檔案清單
				// 置換
				String newFileName = f.getPath().replace(inputPath, outputPaath);
				FamilyMartFileTransduceService.run(newFileName,f);
				// 
				addLog("INFO","寫入檔案："+outputPaath+f.getName());
				plusProgress();
			}
			addLog("INFO", "轉換成功");
			pnlStatusColor.setBackground(Color.GREEN);
		} catch (Exception e) {
			addLog("EROR", "錯誤：" + e.getLocalizedMessage());
			pnlStatusColor.setBackground(Color.RED);
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
		txtLog.append(sfabs.format(new Date()) + " [" + level.toUpperCase() + "] " + content + "\r\n");
	}
}
