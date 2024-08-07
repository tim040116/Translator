package etec.framework.excel_maker.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * <h1>讀取excel檔案</h1>
 * <p>整理Apache POI框架的一些常用語法</p>
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #}
 * <h2>異動紀錄</h2>
 * <br>2024年7月10日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class POIUtil {

	//讀檔
	public static Workbook read(String path) throws IOException {
		Workbook wb = null;
		String extString = path.substring(path.lastIndexOf("."));
		InputStream is = new FileInputStream(path);
		if(".xls".equals(extString)){
		   wb = new HSSFWorkbook(is);
		}else if(".xlsx".equals(extString)){
		   wb = new XSSFWorkbook(is);
		}
		return wb;
	}
	
	/**
	 * @deprecated	POI原生語法夠乾淨了，此段程式僅為了保留產檔語法範本以供日後參考
	 * */
	public static void write(String path,SXSSFWorkbook wb) throws FileNotFoundException, IOException {
		try(FileOutputStream fileOut = new FileOutputStream(path);){
			// 大量資料用 SXSSFWorkbook
			
			// 設定幾筆之後，就先寫到硬碟的暫存檔
			Sheet sheet      = wb.createSheet();
			Row row   = null;
			Cell cell = null;
			for(int r = 0; r < 9; r++){
			    row = sheet.createRow(r);
			    for(int c = 0; c < 9; c++){
			        cell = row.createCell(c);
			        cell.setCellValue(
			            (c + 1) + " x " + (r + 1) + " = " + ((r + 1) * (c + 1)));
			    }
			}
			wb.write(fileOut);
		}
	}
	
	//判斷是否為空行
	public static boolean hasData(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) {
                return true;
            }
        }
        return false;
    }
	
	//強制轉換資料型態成字串
	public static String getStringValue(Cell cell) {
		String res = null;
		if (cell != null) {
	        switch (cell.getCellType()) {
	            case STRING:
	            	res = cell.getStringCellValue();
	                break;
	            case NUMERIC:
	                res = String.valueOf(cell.getNumericCellValue());
	                break;
	            case BOOLEAN:
	            	res = String.valueOf(cell.getBooleanCellValue());
	                break;
	            case ERROR:
	            	res = String.valueOf(cell.getErrorCellValue());
	                break;
	            case FORMULA:
	            	res = cell.getCellFormula();
	                break;
	            case BLANK:
	                break;
	            default:
	        }
		}
		return res;
	}
}
