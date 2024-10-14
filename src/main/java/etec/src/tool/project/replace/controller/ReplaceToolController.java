package etec.src.tool.project.replace.controller;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import etec.framework.code.interfaces.Controller;
import etec.framework.file.readfile.service.FileTool;
import etec.src.tool.project.replace.service.ReplaceToolService;

public class ReplaceToolController implements Controller {

	private static SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
	
	JTextArea txtLog;
	
	JProgressBar progressBar;
	
	JPanel pnlStatusColor;
	
	long pgbTotal;
	long pgbLen;
	
	@Override
	public void run(Map<String,Object> args) throws Exception {
		
		String inputPath = (String)args.get("inputPath");
		String outputPaath = (String)args.get("outputPath");
		String mappingPath = (String)args.get("mappingPath");
		boolean caseInsensitive = (boolean)args.get("caseInsensitive");
		txtLog = (JTextArea)args.get("txtLog");
		pnlStatusColor = (JPanel)args.get("pnlStatusColor");
		progressBar = (JProgressBar)args.get("progressBar");
		//初始化
		reset();
		addLog("INFO","程式啟動");
		pnlStatusColor.setBackground(Color.ORANGE);
		
		//取得取代清單
		addLog("INFO","取得取代清單：" + mappingPath);
		List<String[]> lstrpr = ReplaceToolService.getReplaceList(mappingPath);
		addLog("INFO","讀取完成，共 " + lstrpr.size() + " 項轉換");
		// 取得檔案清單
		List<File> lf = FileTool.getFileList(inputPath);
		pgbTotal = lf.size() * lstrpr.size();
		addLog("INFO","取得檔案清單，共："+lf.size()+" 個檔案");
		
		for(File f : lf) {
			addLog("INFO","讀取檔案");
			String newContent = FileTool.readFile(f);
			for(String[] rpr : lstrpr) {
				System.out.println(rpr[0]+"->"+rpr[1]);
			}
			
			//寫入檔案
			FileTool.createFile(outputPaath+f.getName(), newContent,Charset.forName("utf-8"));
		}
		
	}
	public void reset() {
		txtLog.setText("");
		pgbLen = 0;
		progressBar.setValue(0);
		pnlStatusColor.setBackground(Color.BLACK);
	}
	public void addLog(String level,String content) {
		txtLog.append(sfabs.format(new Date())+ " ["+level.toUpperCase()+"] " + content + "\r\n");
	}
	
	
}
