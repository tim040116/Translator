package main;

import java.io.IOException;

import etec.src.translator.project.azure.fm.hist_export.service.CreateExpTPTService;

/**
 * 列出source target table清單
 * 
 * 
 * */
public class Main3 {

	public static void main(String[] args) {
		try {
			CreateExpTPTService cets = new CreateExpTPTService(null,null,null);
			cets.createFile("C:\\Users\\user\\Desktop\\Trans\\Assessment_Result\\20241225_142100000\\","C:\\Users\\user\\Desktop\\Trans\\Target\\TPT轉換作業.xlsx");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("");

	}



	
}


