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
public class ScanSrcTrgTableService extends BasicScanService{
	
	public ScanSrcTrgTableService(SXSSFWorkbook wb) {
		super(wb,"Source Target",new String[] {"檔名","類型","來源檔","目標檔"});
	}

	public void getSrcTrg(String fileName, String content) throws FileNotFoundException, IOException {
		Log.info("開始分析 source target : " + fileName);
		String res = content;
		res = p_getMerge(fileName, res);
		res = p_getCTAS(fileName, res);
		res = p_getIns(fileName, res);
		res = p_getUpd(fileName, res);	
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
	
	private String p_getUpd(String fileName, String content) {
		// 先做merge 做完刪除自喘以避免關鍵字重複
		StringBuffer sbc = new StringBuffer();
		String regc = "(?i)UPDATE\\s+(?<target>[\\w.]+)\\s+SET(?<source>[^;]+);";// 到最底，為了組rerun
		Matcher mc = Pattern.compile(regc).matcher(content);
		while (mc.find()) {
			// 1-1.取得target跟temp table
			String target = mc.group("target");
			String source = mc.group("source");
			SrcTrgModel model = new SrcTrgModel();

			model.setType("UPDATE");
			model.setTrgTable(target.toUpperCase());
			model.setFileName(fileName);
			model.setSrcTable(p_getSrcTable(source));

			putSrcTrg(model);
			mc.appendReplacement(sbc, "");
		}
		mc.appendTail(sbc);
		return sbc.toString();
	}

	private String p_getIns(String fileName, String content) {
		// 先做merge 做完刪除自喘以避免關鍵字重複
		StringBuffer sbc = new StringBuffer();
		String regc = "(?i)INSERT\\s+INTO\\s+(?<target>[\\w.]+)\\s*(?:\\([\\w,\\s]*?\\))?\\s+SELECT\\s+(?<source>[^;]+);";// 到最底，為了組rerun
		Matcher mc = Pattern.compile(regc).matcher(content);
		while (mc.find()) {
			// 1-1.取得target跟temp table
			String target = mc.group("target");
			String source = mc.group("source");
			SrcTrgModel model = new SrcTrgModel();

			model.setType("INSERT");
			model.setTrgTable(target.toUpperCase());
			model.setFileName(fileName);
			model.setSrcTable(p_getSrcTable(source));

			putSrcTrg(model);
			mc.appendReplacement(sbc, "");
		}
		mc.appendTail(sbc);
		return sbc.toString();
	}

	private String p_getCTAS(String fileName, String content) {
		// 先做merge 做完刪除自喘以避免關鍵字重複
		StringBuffer sbc = new StringBuffer();
		String regc = "(?i)CREATE\\s+[\\w ]+?TABLE\\s+(?<target>[\\w.]+)\\s+AS\\s*\\((?<source>[^;]+);";// 到最底，為了組rerun
		Matcher mc = Pattern.compile(regc).matcher(content);
		while (mc.find()) {
			// 1-1.取得target跟temp table
			String target = mc.group("target");
			String source = mc.group("source");
			SrcTrgModel model = new SrcTrgModel();

			model.setType("CTAS");
			model.setTrgTable(target.toUpperCase());
			model.setFileName(fileName);
			model.setSrcTable(p_getSrcTable(source));

			putSrcTrg(model);
			mc.appendReplacement(sbc, "");
		}
		mc.appendTail(sbc);
		return sbc.toString();
	}

	private String p_getMerge(String fileName, String content) {
		// 先做merge 做完刪除自喘以避免關鍵字重複
		StringBuffer sbm = new StringBuffer();
		String regm = "(?i)MERGE\\s+INTO\\s+(?<target>[\\w.]+)[^;]*USING\\s*(?<source>[^;]+);";// 到最底，為了組rerun
		Matcher mm = Pattern.compile(regm).matcher(content);
		while (mm.find()) {
			// 1-1.取得target跟temp table
			String target = mm.group("target");
			String source = mm.group("source");
			SrcTrgModel model = new SrcTrgModel();

			model.setType("MERGE");
			model.setTrgTable(target.toUpperCase());
			model.setFileName(fileName);
			model.setSrcTable(p_getSrcTable(source));

			putSrcTrg(model);
			mm.appendReplacement(sbm, "");
		}
		mm.appendTail(sbm);
		return sbm.toString();
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
