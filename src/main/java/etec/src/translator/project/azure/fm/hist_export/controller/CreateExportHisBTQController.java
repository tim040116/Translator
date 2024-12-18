package etec.src.translator.project.azure.fm.hist_export.controller;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import etec.src.translator.project.azure.fm.hist_export.model.TableModel;
import etec.src.translator.project.azure.fm.hist_export.service.CreateExportHisBTQService;

/**
 * 對應新版UI
 *
 * @author Tim
 * @since 6.0.1.0
 **/
public class CreateExportHisBTQController implements Controller {

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

			List<TableModel> lst = new ArrayList<TableModel>();
			// 儲存參數
			reset();
			addLog("資訊", "開始產檔");
			pnlStatusColor.setBackground(Color.ORANGE);
			addLog("資訊", "取得資料目錄 : " + inputPath);
			addLog("資訊", "取得產檔目錄 : " + outputPath);
			if(!inputPath.matches(".*\\.xlsx")) {
				addLog("錯誤", "參數錯誤，參數檔請使用.xlsx格式");
				pnlStatusColor.setBackground(Color.RED);
				return;
			}
			// 取得參數檔
			addLog("資訊", "正在開啟檔案，請稍後...");
			Workbook wb = POIUtil.read(inputPath);
			//解析Table
			addLog("資訊","開始解析資料表設定");
			addLog("資訊","解析Table參數");
			Sheet shtTbl = wb.getSheet("Table");
			pgbLen = 0;
			pgbTotal = shtTbl.getPhysicalNumberOfRows();
			boolean isFirstRow = true;
			for(Row row : shtTbl) {
				if (isFirstRow) {
					isFirstRow = false;
					continue;
				}
				TableModel model = new TableModel();
				model.setDbNm(POIUtil.getStringValue(row.getCell(0)).trim().toLowerCase());
				model.setTblNm(POIUtil.getStringValue(row.getCell(1)).trim().toLowerCase());
				model.setCondition(POIUtil.getStringValue(row.getCell(2)));
				model.setFilePath(POIUtil.getStringValue(row.getCell(3)).trim());
				model.setFileNm(POIUtil.getStringValue(row.getCell(4)).trim());
				lst.add(model);
				plusProgress();
			}
			addLog("資訊","解析完成，共 " + (pgbTotal - 1) + " 筆資料");
			//解析Column
			addLog("資訊","解析Column參數");
			isFirstRow = true;
			Sheet shtCol = wb.getSheet("column");
			if(shtCol==null) {
				shtCol = wb.getSheet("Columns");
			}
			pgbLen = 0;
			pgbTotal = shtCol.getPhysicalNumberOfRows();
			for(Row row : shtCol) {
				if (isFirstRow) {
					isFirstRow = false;
					continue;
				}
				String dbNm = POIUtil.getStringValue(row.getCell(0)).trim().toLowerCase();
				String tblNm = POIUtil.getStringValue(row.getCell(1)).trim().toLowerCase();
				String column = POIUtil.getStringValue(row.getCell(2)).trim().toLowerCase();
				for(TableModel tbl : lst) {
					if(tbl.match(dbNm, tblNm)) {
						tbl.getLstColumn().add(column);
					}
				}
				plusProgress();
			}
			addLog("資訊","解析完成，共 " + (pgbTotal - 1) + " 筆資料");
			addLog("資訊", "開始產檔");
			pgbLen = 0;
			pgbTotal = lst.size();
			Map<String,Integer> mapFileCnt = new HashMap<String,Integer>();
			for(TableModel tbl : lst) {
				String dataFile = (tbl.getFilePath()+tbl.getFileNm());
				String dbtb = tbl.getDbNm()+"."+tbl.getTblNm();
				String where = tbl.getCondition();
				String yyyymm = where.replaceAll("(?i)[\\S\\s]+BETWEEN\\s+([\\w${}]+)\\s+AND\\s*[\\S\\s]+", "$1");
//				產生序號
				if(!yyyymm.matches("\\d{8}")) {
					if(yyyymm.matches("\\$\\{\\w+\\}")) {
						yyyymm = yyyymm.replaceAll("[${}]", "");
					} else {
						mapFileCnt.compute(tbl.getTblNm(), (key, oldValue) -> oldValue == null ? 1 : oldValue + 1);
						yyyymm = "0".repeat(8-String.valueOf(mapFileCnt.get(tbl.getTblNm())).length())+mapFileCnt.get(tbl.getTblNm());
						
					}
				}
				String btqFile = outputPath + tbl.getTblNm() + "_" + yyyymm + ".btq";
//				String btqFile = outputPath + tbl.getTblNm() + "_4YM.btq";
//				where = where.replaceAll("(?i)\\s*(\\S+)\\s+BETWEEN\\s+[\\S\\s]+", "$1") + " BETWEEN ${TX4YMB} AND ${TX4YME}";
				String content = CreateExportHisBTQService.buildContent(dataFile,tbl.getDbNm(),tbl.getTblNm(),tbl.getLstColumn(), where);
				addLog("資訊", "產生檔案："+btqFile);
				FileTool.createFile(btqFile, content);
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
}
