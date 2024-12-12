package etec.src.assignment.project.prg_scan.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import etec.framework.security.log.service.Log;
import etec.src.assignment.project.prg_scan.model.SrcTrgModel;

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
public class ScanExportService extends BasicScanService{
	
	public ScanExportService(SXSSFWorkbook wb) {
		super(wb,"Export",new String[] {"檔名","類型","來源檔","目標檔"});
	}

	public void getSrcTrg(String fileName, String content) throws FileNotFoundException, IOException {
		Log.info("開始分析 source target : " + fileName);
		String res = content;
		p_getExport(fileName, res);
		
		Log.info("分析完成");
	}
	
	/**
	 * 寫入src trg table
	 * */
	public void putSrcTrg(SrcTrgModel model) {
		Log.info("寫入資料：" + model.getFileName()+"\t"+model.getTrgTable());
		for (String srcTbl : model.getSrcTable()) {
			addRow(new String[] {
				 model.getFileName()
				,model.getType()
				,srcTbl
				,model.getTrgTable()
			});
		}
	}
	
	private void p_getExport(String fileName, String content) {
		// 先做merge 做完刪除自喘以避免關鍵字重複
		String rege = "(?i)\\.EXPORT\\s+REPORT\\s+[^=]+=(?<exportfile>\\s*[^\\r\\n]+)[\\S\\s]+?(?<sql>SEL(?:ECT)?\\s+[^;]+;)[\\s\\S]*?\\.EXPORT\\s+RESet\\s*;";// 到最底，為了組rerun
		Matcher me = Pattern.compile(rege).matcher(content);
		while (me.find()) {
			// 1-1.取得target跟temp table
			String exportfile = me.group("exportfile");
			String sql = me.group("sql");
			SrcTrgModel model = new SrcTrgModel();

			model.setType("EXPORT");
			model.setTrgTable(exportfile.trim());
			model.setFileName(fileName);
			model.setSrcTable(p_getSrcTable(sql));

			putSrcTrg(model);
		}
	}


	private static List<String> p_getSrcTable(String source) {
		List<String> srcTable = new LinkedList<String>();
		String regms = "(?i)"+getSchemaName()+"\\.\\w+";
		Matcher mms = Pattern.compile(regms).matcher(source);
		while (mms.find()) {
			if (!srcTable.contains(mms.group().toUpperCase())) {
				srcTable.add(mms.group().toUpperCase());
			}
		}
		return srcTable;
	}
	
}
