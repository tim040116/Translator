package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.file.readfile.service.FileTool;

public class Main3 {

	private static List<SrcTrgModel> lstAll = new ArrayList<SrcTrgModel>();
	
	
	private static String[] arrSchema = {
			 "PDATA"
			,"PMART"
			,"PTEMP"
			,"PSTAGE"
	};
	public static void main(String[] args) {

		try {
			List<File> lf = null;
			lf = FileTool.getFileList("C:\\Users\\user\\Desktop\\Trans\\T0\\job_20241121\\job_20241121");

			for (File f : lf) {
				String content = FileTool.readFile(f);
				ConvertRemarkSafely.savelyConvert(content,(t)->{
					t = t
						.replaceAll("(?i)\\Q${DATA}\\E","PDATA")
						.replaceAll("(?i)\\Q${MART}\\E","PMART")
						.replaceAll("(?i)\\Q${TEMP}\\E","PTEMP")
						.replaceAll("(?i)\\Q${STAGE}\\E","PSTAGE");
					srcTrg(f.getName(),t);
					return t;
				});
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String srcTrg(String fileName, String content) {
		String res = content;
		
		res = doMerge(fileName,res);
		
		
		StringBuffer sbc = new StringBuffer();
		String regc = "(?i)CREATE\\s+[\\w ]+?TABLE\\s+\\w+";// 到最底，為了組rerun
		Matcher mc = Pattern.compile(regc).matcher(content);
		while (mc.find()) {
			
		}
		
		
		
		
		
	
		return res;
	}

	private static String doMerge(String fileName,String content) {
		// 先做merge 做完刪除自喘以避免關鍵字重複
				StringBuffer sbm = new StringBuffer();
				String regm = "(?i)MERGE\\s+INTO\\s+(?<target>\\S+)[^;]*USING\\s*(?<source>[^;]+);";// 到最底，為了組rerun
				Matcher mm = Pattern.compile(regm).matcher(content);
				while (mm.find()) {
					// 1-1.取得target跟temp table
					String targetTable = mm.group("target");
					String source = mm.group("source");
					SrcTrgModel model = new SrcTrgModel();
					
					model.type = "MERGE";
					model.trgTable = targetTable.toUpperCase();
					model.fileName = fileName;
					
					
					String regms = "(?i)(?:\\Q"+String.join("\\E|\\Q",arrSchema)+"\\E)\\.\\w+";
					Matcher mms = Pattern.compile(regms).matcher(source);
					while(mms.find()) {
						if(model.srcTable.contains(mms.group().toUpperCase()))
						model.srcTable.add(mms.group());
					}
					lstAll.add(model);
					mm.appendReplacement(sbm, "");
				}
				mm.appendTail(sbm);
				return sbm.toString();
	}
}

class SrcTrgModel {

	public String fileName;

	public String type;

	public String trgTable;

	public List<String> srcTable = new ArrayList<String>();

}