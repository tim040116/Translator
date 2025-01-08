package temp;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import etec.framework.file.readfile.service.FileTool;
import etec.src.translator.sql.gp.translater.GreenPlumTranslater;

public class UIXMLMain {
	
	public static String path = "C:\\Users\\user\\Desktop\\Trans\\T0\\xml_to_xlsx\\WebWDWH.xml";
	
	public static String outputPath = "C:\\Users\\user\\Desktop\\Trans\\Assessment_Result\\WebWDWH.xlsx";
	
	public static void main(String[] args) {
		try (
				FileOutputStream fo = new FileOutputStream(outputPath);
				SXSSFWorkbook wb = new SXSSFWorkbook();
			){
			String content = FileTool.readFile(path);
			content = content.replaceAll("</?tag>", "");
			SXSSFSheet sht = wb.createSheet("IGA");
			//header
			String[] columns = {
					 "pro_id"
					,"mod_id"
					,"qi_id"
					,"qi_name"
					,"sql_text_td"
					,"sql_text_pg"
					,"cache"
			};
			Row headerRow = sht.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
			}
			
			int rowId = 1;
			
			Matcher mqi = Pattern.compile("(?i)<QueryInstance>\\s*([\\S\\s]+?)</QueryInstance>").matcher(content);
			while(mqi.find()) {
				Map<String,String> map = new HashMap<String,String>();
				String str = mqi.group(1);
				Matcher mtag = Pattern.compile("<(\\w+)>([\\S\\s]+?)</\\1>").matcher(str);
				while(mtag.find()) {
					map.put(mtag.group(1),mtag.group(2));
				}
				System.out.println(str);
				String sqlTextTd = GreenPlumTranslater.translate(map.get("sql_text"))
						.replaceAll("<!\\[CDATA\\[([\\S\\s]+?)\\]\\]>", "$1").trim();
				String sqlTextPg = GreenPlumTranslater.translate(sqlTextTd);
				sqlTextPg = sqlTextPg.replaceAll("(?i)(PMART|PDATA)\\.", "public.");
				Row row = sht.createRow(rowId);
				row.createCell(0).setCellValue(map.get("pro_id"));
				row.createCell(1).setCellValue(map.get("mod_id"));
				row.createCell(2).setCellValue(map.get("qi_id"));
				row.createCell(3).setCellValue(map.get("qi_name"));
				row.createCell(4).setCellValue(sqlTextTd);
				row.createCell(5).setCellValue(sqlTextPg);
				row.createCell(6).setCellValue(map.get("cache"));
				rowId++;
			}
			wb.write(fo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("finish");
	}
}
