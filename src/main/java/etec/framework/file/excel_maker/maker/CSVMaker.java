package etec.framework.file.excel_maker.maker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import etec.common.exception.MissingAnnotationException;
import etec.framework.file.excel_maker.annotation.SheetModel;
import etec.framework.file.excel_maker.model.Worksheet;

/**
 * <h1>將 domain 轉換成 csv</h1>
 * <p></p>
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #}
 * <h2>異動紀錄</h2>
 * <br>2024年6月12日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class CSVMaker{

	private boolean isWriteTitle = true;
	
	private Charset charset = Charset.forName("UTF-8"); 
	
	private File file;
	
	private BufferedWriter bw;
	
	@SuppressWarnings("rawtypes")
	private Map<String,Worksheet> mapSheet = new HashMap<String,Worksheet>();
	
	/**
	 * 活躍頁籤
	 * */
	private String activeSheetName = "";
	
	/**
	 * <h1></h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月12日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	domain	包含{@link SheetModel}的物件
	 * @throws	MissingAnnotationException
	 * @see		
	 * @return	void
	 * @throws IOException 
			 */
	public void addLine(Object domain) throws MissingAnnotationException, IOException {
		
		bw.write("");
	}
	
	
	public CSVMaker(String fileName) throws MissingAnnotationException, IOException{
		//取得類別
		@SuppressWarnings("unchecked")
		Class<Object> ct = (Class<Object>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//確認是否有註解
		if(!ct.getClass().isAnnotationPresent(SheetModel.class)) {
			throw new MissingAnnotationException(SheetModel.class);
		}
		//沒檔案的話產檔
		file = new File(fileName);
		file.getParentFile().mkdirs();
		if (!file.exists()) {
			file.createNewFile();
		}
		bw = Files.newBufferedWriter(Paths.get(fileName), charset, StandardOpenOption.APPEND);
	}
	
	/**
	 * <h1>進入指定活頁簿</h1>
	 * <p>
	 * <br>將該頁籤設定為活躍頁籤
	 * <br>若此頁籤上不存在，則會建立一個新的頁籤
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年6月19日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	e
	 * @see		
	 * @return	return_type
	 */
	public <T> void findSheet(Class<T> c) throws MissingAnnotationException, IllegalArgumentException, IllegalAccessException {
		//確認是否有註解
		if(!c.getClass().isAnnotationPresent(SheetModel.class)) {
			throw new MissingAnnotationException(SheetModel.class);
		}
		//取得title
		activeSheetName = c.getAnnotation(SheetModel.class).sheetName();
		//如果沒有註冊該活頁簿，則新建立一個
		if(!mapSheet.containsKey(activeSheetName)) {
			mapSheet.put(activeSheetName, new Worksheet<T>());
		}
		
		
	}
	
	public void close() throws IOException {
		if (bw == null) {
            return;
        }
		bw.close();
	}
	
	public void setWriteTitle(boolean isWriteTitle) {
		this.isWriteTitle = isWriteTitle;
	}
	
	public void setCharset(Charset charset) {
		this.charset = charset;
	}
}
