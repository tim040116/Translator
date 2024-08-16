package test.td.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.sql.td.service.CreateSDIService;

public class TestCreateSDIService {
	
	public static void run() {
		try {
			List<File> lf = FileTool.getFileList("C:\\Users\\User\\Desktop\\台企銀\\");
			for(File f : lf) {
				CreateSDIService.createSDI(f);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
