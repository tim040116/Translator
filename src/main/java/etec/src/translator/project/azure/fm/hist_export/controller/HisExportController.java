package etec.src.translator.project.azure.fm.hist_export.controller;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import etec.src.translator.project.azure.fm.hist_export.service.CreateExpHisBTQService;

/**
 * 應皓鈞要求，
 * 讀取excel參數檔
 * 產生對應的btq排程檔已匯出歷史資料
 *	
 * @author Tim
 * @since 6.0.1.0
 **/
public class HisExportController implements Controller {

	private static SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");

	JTextArea txtLog;

	@Override
	public void run(Map<String, Object> args) throws Exception {
		txtLog = (JTextArea) args.get("txtLog");
		Controller con = null;
		switch ((String)args.get("fileType")) {
		case "BTQ":
			con = new CreateExpHisBTQController();
			break;
		case "TPT":
			con = new CreateExpHisTPTController();
			break;
		default:	
			break;
		}
		con.run(args);
	}
	
	public void addLog(String level, String content) {
		Log.info(content);
		txtLog.append(sfabs.format(new Date()) + " [" + level.toUpperCase() + "] " + content + "\r\n");
	}

	@Override
	public Map<String, Object> getArgs() {
		Map<String, Object> map = new TreeMap<String,Object>();
		map.put("arrFileType",new String[] {
			 "BTQ"
			,"TPT"
		});
		return map;
	}
}
