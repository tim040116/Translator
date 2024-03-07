package main;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.annotation.Application;
import etec.common.utils.convert_safely.ConvertRemarkSafely;
import etec.src.sql.gp.translater.GreemPlumTranslater;
import etec.view.application.FastTransduceApplication;
/**
 * @author	Tim
 * @since	2023年10月11日
 * 
 * 
 * */
public class Main {
	
	static String folder = "C:\\Users\\User\\Desktop\\familymart\\T1\\SQLAExport.txt";
	
	public static void main(String[] args) {
		try {
			String now = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
			Application app = (Application)FastTransduceApplication.class.getDeclaredAnnotationsByType(Application.class)[0];
			System.out.println(app.value());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
