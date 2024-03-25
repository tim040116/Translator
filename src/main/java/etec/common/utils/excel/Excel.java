package etec.common.utils.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel {
	
//	private static final String SAMPLE_PATH = "src/main/resources/META-INF/sample/xls/";
//	
//	private Workbook workbook;
//	
//	private Excel(File f) throws IOException {
//		workbook = setWorkbook(f);
//	}
//	
//	public static Excel readFromResource(String fileName) throws IOException {
//		return new Excel(new File(SAMPLE_PATH+fileName));
//	}
//	
//	/**
//	 * <h1>寫成檔案</h1>
//	 * <p></p>
//	 * 
//	 * <h2>異動紀錄</h2>
//	 * <br>2024年3月18日	Tim	建立功能
//	 * 
//	 * @author	Tim
//	 * @since	4.0.0.0
//	 * @param	filePath	產檔路徑及檔名
//	 * @throws	IOException
//	 * @see
//	 * @return	
//			 */
//	public void writeFile(String filePath) throws IOException {
//		try (FileOutputStream out = new FileOutputStream(filePath)) {
//			workbook.write(out);
//		} 
//	}
//	
//	/**
//	 * <h1>匯入Workbook</h1>
//	 * <p></p>
//	 * <p></p>
//	 * 
//	 * <h2>異動紀錄</h2>
//	 * <br>2024年3月18日	Tim	建立功能
//	 * 
//	 * @author	Tim
//	 * @since	4.0.0.0
//	 * @param	file	excel檔
//	 * @throws	IOException
//	 * @see
//	 * @return	
//			 */
//	private Workbook setWorkbook(File file) throws IOException {
//		Workbook wb = null; 
//		InputStream is = new FileInputStream(file);
//		if(file.getName().matches(".*\\.xls")){
//			wb = new HSSFWorkbook(is);
//		}else if(file.getName().matches(".*\\.xlsx")){
//			wb = new XSSFWorkbook(is);
//		}
//		return wb;
//	}
}
