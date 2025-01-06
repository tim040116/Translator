package etec.src.translator.project.azure.fm.hist_export.controller;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import etec.framework.code.interfaces.Controller;
import etec.framework.file.excel_maker.util.POIUtil;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.src.translator.project.azure.fm.hist_export.model.CreateExpTPTModel;
import etec.src.translator.project.azure.fm.hist_export.model.TableModel;
import etec.src.translator.project.azure.fm.hist_export.service.CreateExpHisBTQService;
import etec.src.translator.project.azure.fm.hist_export.service.CreateExpTPTService;

/**
 * 應皓鈞要求，
 * 讀取excel參數檔
 * 產生對應的btq排程檔已匯出歷史資料
 *	
 * @author Tim
 * @since 6.0.1.0
 **/
public class CreateExpHisTPTController implements Controller {

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
			String outputPath = (String) args.get("outputPath") + "\\"	+ (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date()) + "\\src\\";
			txtLog = (JTextArea) args.get("txtLog");
			pnlStatusColor = (JPanel) args.get("pnlStatusColor");
			progressBar = (JProgressBar) args.get("progressBar");
			CreateExpTPTService cetts = new CreateExpTPTService(txtLog,progressBar,pnlStatusColor);
			// 儲存參數
			reset();
			addLog("資訊", "開始執行，產生檔案 TPT");
			pnlStatusColor.setBackground(Color.ORANGE);
			addLog("資訊", "取得資料目錄 : " + inputPath);
			addLog("資訊", "取得產檔目錄 : " + outputPath);
			if(!inputPath.matches(".*\\.xlsx")) {
				addLog("錯誤", "參數錯誤，參數檔請使用.xlsx格式");
				pnlStatusColor.setBackground(Color.RED);
				return;
			}
			// 取得參數檔
			Map<String,CreateExpTPTModel> mapData = cetts.readExcel(inputPath);
			
			addLog("資訊", "開始轉換語法");
			pgbLen = 0;
			pgbTotal = mapData.size();
			for(Entry<String,CreateExpTPTModel> en : mapData.entrySet()) {
				CreateExpTPTModel m = en.getValue();
				String strtpt = cetts.buildTPTContent(m);
				String strbtq = cetts.buildBTQContent(m);
				cetts.writeFile(outputPath,m.getAzTable(),strtpt,strbtq);
				plusProgress();
			}
			addLog("資訊", "產檔成功，共 " + pgbTotal + " 筆資料");
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
		float val = (pgbLen * 100 / pgbTotal);
		progressBar.setValue((int)val);
		progressBar.setString((int)val + " %");
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
