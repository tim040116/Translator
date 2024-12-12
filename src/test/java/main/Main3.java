package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import etec.framework.context.convert_safely.service.ConvertRemarkSafely;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.log.service.Log;
import etec.src.assignment.project.prg_scan.service.ScanExportService;
import etec.src.assignment.project.prg_scan.service.ScanFileListService;
import etec.src.assignment.project.prg_scan.service.ScanSPService;
import etec.src.assignment.project.prg_scan.service.ScanSrcTrgTableService;

/**
 * 列出source target table清單
 * 
 * 
 * */
public class Main3 {


//	private static String path = "C:\\Users\\user\\Desktop\\Trans\\Target\\";
	
	
	private static String path = "C:\\Users\\user\\Desktop\\Trans\\T0\\job_20241121\\job_20241121\\";
	
	
	private static String strFileOut = "C:\\Users\\user\\Desktop\\Trans\\Assessment_Result\\Scan Result.xlsx";
	
	

	public static void main(String[] args) {

		try(
				FileOutputStream fo = new FileOutputStream(strFileOut);
				SXSSFWorkbook wb = new SXSSFWorkbook();
			){
			List<File> lf = null;
			lf = FileTool.getFileList(path);
			ScanFileListService fls = new ScanFileListService(wb);
			ScanSrcTrgTableService stts = new ScanSrcTrgTableService(wb);
			ScanSPService sps = new ScanSPService(wb);
			ScanExportService es = new ScanExportService(wb);
			
			for (File f : lf) {
				fls.addFileList(path,f);
				String content = FileTool.readFile(f);
				content = ConvertRemarkSafely.cleanRemark(content);
				content = content
						.replaceAll("(?i)\\Q${DATA}\\E", "PDATA")
						.replaceAll("(?i)\\Q${MART}\\E", "PMART")
						.replaceAll("(?i)\\Q${TEMP}\\E", "PTEMP")
						.replaceAll("(?i)\\Q${STAGE}\\E", "PSTAGE");
				
				stts.getSrcTrg(f.getPath().replace(path, ""), content);
				sps.getSP(f.getPath().replace(path, ""), content);
				es.getSrcTrg(f.getPath().replace(path, ""), content);
			}
			wb.write(fo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.info("產製完成");
	}



	
}


