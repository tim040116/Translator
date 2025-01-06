package etec.src.translator.project.azure.fm.hist_export.service;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import etec.framework.file.excel_maker.util.POIUtil;
import etec.framework.file.params.service.ResourceTool;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.src.translator.project.azure.fm.hist_export.model.CreateExpTPTModel;
/**
 * <h1></h1>
 * <p></p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年12月25日	Tim	建立功能
 * 
 * @author	Tim
 * @since	6.0.3.0
 */
public class CreateExpTPTService {
	
	private static SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
	
	private JTextArea txtLog;

	private JProgressBar progressBar;

	private JPanel pnlStatusColor;
	
	long pgbTotal;
	
	long pgbLen;
	
	public CreateExpTPTService(JTextArea txtLog,JProgressBar progressBar,JPanel pnlStatusColor) {
		super();
		this.txtLog = txtLog;
		this.progressBar = progressBar;
		this.pnlStatusColor = pnlStatusColor;
	}
	
	public void createFile(String writeFileDir,String path) throws IOException {
		readExcel(path);
//		for(Entry<String,CreateExpTPTModel> en : map.entrySet()) {
//			CreateExpTPTModel m = en.getValue();
//			String strtpt = p_buildTPTContent(m);
//			String strbtq = p_buildBTQContent(m);
//			writeFile(writeFileDir,m.getAzTable(),strtpt,strbtq);
//		}
	}
	
	//讀取excel檔
	public Map<String,CreateExpTPTModel> readExcel(String path) throws IOException {
		addLog("資訊","開始解析資料表設定");
		Map<String,CreateExpTPTModel> map = new TreeMap<String,CreateExpTPTModel>();
		addLog("資訊","正在開啟檔案，請稍後...");
		Workbook wb = POIUtil.read(path);
		addLog("資訊","開始解析Table參數");
		Sheet shtTbl = wb.getSheet("Table");
		pgbLen = 0;
		pgbTotal = shtTbl.getPhysicalNumberOfRows();
		boolean isFirstRow = true;
		//讀Table
		for(Row row : shtTbl) {
			if (isFirstRow) {
				isFirstRow = false;
				continue;
			}
			CreateExpTPTModel model = new CreateExpTPTModel();
			model.setTdSchema(POIUtil.getStringValue(row.getCell(0)).trim().toLowerCase());
			model.setTdTable(POIUtil.getStringValue(row.getCell(1)).trim().toLowerCase());
			model.setWhere(POIUtil.getStringValue(row.getCell(2)).trim());
			model.setDir(POIUtil.getStringValue(row.getCell(3)).trim());
			map.put(model.getTdSchema()+"."+model.getTdTable(),model);
			plusProgress();
		}
		addLog("資訊","解析完成，共 " + (pgbTotal - 1) + " 筆資料");
		//讀column
		addLog("資訊","開始解析Column參數");
		Sheet shtCol = wb.getSheet("column");
		if(shtCol==null) {
			shtCol = wb.getSheet("Columns");
		}
		pgbLen = 0;
		pgbTotal = shtCol.getPhysicalNumberOfRows();
		isFirstRow = true;
		for(Row row : shtCol) {
			if (isFirstRow) {
				isFirstRow = false;
				plusProgress();
				continue;
			}
			String tddb = POIUtil.getStringValue(row.getCell(0)).trim().toLowerCase();
			String tdtb = POIUtil.getStringValue(row.getCell(1)).trim().toLowerCase();
			String tdcl = POIUtil.getStringValue(row.getCell(2)).trim();
			String azdb = POIUtil.getStringValue(row.getCell(3)).trim().toLowerCase();
			String aztb = POIUtil.getStringValue(row.getCell(4)).trim().toLowerCase();
			String azcl = POIUtil.getStringValue(row.getCell(5)).trim();
			if("".equals(tddb+tdtb+azdb+aztb)||"fdp_upt".equals(azcl)) {
				plusProgress();
				continue;
			}
			CreateExpTPTModel model = map.get(tddb+"."+tdtb);
			model.setAzSchema(azdb);
			model.setAzTable(aztb);
			model.getAzCol().add(azcl);
			
			if("".equals(tdcl)) {
				model.getLstSelect().add("''''");
				plusProgress();
				continue;
			}
			model.getLstSelect().add(tdcl);
			model.getLstHeader().add(tdcl.replaceAll("(?i)TO_CHAR\\(\\s*(\\w+)\\s*\\)", "$1"));
			plusProgress();
		}
		addLog("資訊","解析完成，共 " + (pgbTotal - 1) + " 筆資料");
		return map;
	}
	
	//產tpt檔
	public String buildTPTContent(CreateExpTPTModel m) {
		addLog("資訊", "開始產生TPT語法...");
		String tamplate = "sample/fm/exportBTQ/sampleTPT_TPT.tpt";
		Map<String,String> param = new HashMap<String,String>();
		param.put("ETEC_azTbNm",m.getAzTable());
		param.put("ETEC_header",String.join(",",m.getLstHeader()));
		param.put("ETEC_select",String.join("),'''') || ''\",\"'' || \r\n  COALESCE(TRIM(", m.getLstSelect()));
		param.put("ETEC_tdTable",m.getTdSchema()+"."+m.getTdTable());
		param.put("ETEC_where",m.getWhere());
		param.put("ETEC_hisDir",m.getDir());
		ResourceTool rt = new ResourceTool();
		String content = rt.readFile(tamplate,param);
		return content;
	}

	//產btq檔
	public String buildBTQContent(CreateExpTPTModel m) {
		addLog("資訊", "開始產生BTQ語法...");
		String tamplate = "sample/fm/exportBTQ/sampleTPT_BTQ.btq";
		Map<String,String> param = new HashMap<String,String>();
		param.put("ETEC_hisDir",m.getDir());
		param.put("ETEC_azTbNm",m.getAzTable());
		ResourceTool rt = new ResourceTool();
		String content = rt.readFile(tamplate,param);
		return content;
	}
	

	public void writeFile(String dir,String azTbNm,String strtpt,String strbtq) throws IOException {
		String tpt = dir+azTbNm+".tpt";
		String btq = dir+azTbNm+"_TX4YM.btq";
		addLog("資訊", "開始產生檔案："+tpt);
		FileTool.createFile(tpt, strtpt);
		addLog("資訊", "開始產生檔案："+btq);
		FileTool.createFile(btq, strbtq);
		addLog("資訊", "產檔完成");
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
