package etec.framework.excel_maker.maker;

import java.lang.reflect.ParameterizedType;

import etec.common.exception.MissingAnnotationException;
import etec.framework.excel_maker.model.ExcelModel;

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
public class CSVMaker<T>{

	private boolean isWriteTitle = true;
	
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
	 * @param	domain	包含{@link ExcelModel}的物件
	 * @throws	MissingAnnotationException
	 * @see		
	 * @return	void
			 */
	public void writeCSV(T domain) throws MissingAnnotationException {
		
		//取得檔名
		String fileNm = domain.getClass().getAnnotation(ExcelModel.class).fileName()+".csv";
	}
	
	public CSVMaker() throws MissingAnnotationException{
		//取得類別
		@SuppressWarnings("unchecked")
		Class<T> ct = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//確認是否有註解
		if(!ct.getClass().isAnnotationPresent(ExcelModel.class)) {
			throw new MissingAnnotationException(ExcelModel.class);
		}
		
	}
}
