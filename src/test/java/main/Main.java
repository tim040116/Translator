package main;

import etec.common.model.VersionModel;
import etec.framework.file.readfile.service.FileTool;

/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\Trans\\Target";
	
	public static void main(String[] args) {
		try {

			System.out.println(VersionModel.VERSION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
