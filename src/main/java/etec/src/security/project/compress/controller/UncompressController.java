package etec.src.security.project.compress.controller;

import java.util.Map;
import java.util.Scanner;

import etec.framework.code.interfaces.Controller;
import etec.framework.file.readfile.service.BigFileSplitTool;

public class UncompressController implements Controller{
	@Override
	public void run(Map<String,Object> args) throws Exception {
		try {
			Scanner sc = new Scanner(System.in);
			System.out.println("請輸入檔案路徑：");
			String zipPath = sc.next();
			BigFileSplitTool.concatFile(zipPath);
			System.out.println("完成");
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
