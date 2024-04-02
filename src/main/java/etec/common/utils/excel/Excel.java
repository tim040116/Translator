package etec.common.utils.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * <h1>EXCEL 檔案處理</h1>
 * <p>
 * <br>一個物件是一個xls檔
 * <br>對xls進行編輯的功能
 * </p>
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #}
 * <h2>異動紀錄</h2>
 * <br>2024年4月2日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.1
 * @since	4.0.0.1
 * @see		
 */
public class Excel {
	
	/**樣板的路徑
	 * */
	private static final String SAMPLE_PATH = "src/main/resources/sample/xls/";
	
	private Workbook workbook;
	
	private Excel(File f) throws IOException {
		workbook = setWorkbook(f);
	}
	
	/**
	 * <h1>產生固定格式的xls檔</h1>
	 * <p>
	 * <br>
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月2日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.1
	 * @param	fileName	resource檔案的名稱
	 * @param	targetPath	新檔案的路徑及名稱
	 * @throws	IOException
	 * @see
	 * @return	
			 */
	public static Excel copyFromResource(String fileName,String targetPath) throws IOException {
		new Excel(new File(SAMPLE_PATH+fileName)).writeFile(targetPath);
		return new Excel(new File(targetPath));
	}
	
	/**
	 * <h1>讀xls檔</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月2日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.1
	 * @param	
	 * @throws	IOException
	 * @see
	 * @return	
			 */
	public static Excel readFromResource(String fileName) throws IOException {
		return new Excel(new File(SAMPLE_PATH+fileName));
	}
	
	/**
	 * <h1>寫成檔案</h1>
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月18日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.1
	 * @param	filePath	產檔路徑及檔名
	 * @throws	IOException
	 * @see
	 * @return	
			 */
	public void writeFile(String filePath) throws IOException {
		try (FileOutputStream out = new FileOutputStream(filePath)) {
			workbook.write(out);
		} 
	}
	
	/**
	 * <h1>匯入Workbook</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年3月18日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.1
	 * @param	file	excel檔
	 * @throws	IOException
	 * @see
	 * @return	
			 */
	private Workbook setWorkbook(File file) throws IOException {
		Workbook wb = null; 
		InputStream is = new FileInputStream(file);
		if(file.getName().matches(".*\\.xls")){
			wb = new HSSFWorkbook(is);
		}else if(file.getName().matches(".*\\.xlsx")){
			wb = new XSSFWorkbook(is);
		}
		return wb;
	}
}
