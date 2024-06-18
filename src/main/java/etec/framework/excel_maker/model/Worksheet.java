package etec.framework.excel_maker.model;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import etec.common.exception.MissingAnnotationException;
import etec.framework.excel_maker.annotation.ExcelModel;

public class Worksheet<T> {
	
	/**
	 * 活頁簿的名稱
	 * @author	Tim
	 * @since	1.0
	 * */
	private String sheetName;
	
	private String key;
	
	/**
	 * 活頁簿
	 * @author	Tim
	 * @since	1.0
	 * */ 
	private Map<String,T> column;
	
	public Worksheet() throws MissingAnnotationException{
		//取得類別
		@SuppressWarnings("unchecked")
		Class<Object> ct = (Class<Object>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//確認是否有註解
		if(!ct.getClass().isAnnotationPresent(ExcelModel.class)) {
			throw new MissingAnnotationException(ExcelModel.class);
		}
		//取得活頁簿名稱
		this.sheetName = ct.getAnnotation(ExcelModel.class).fileName();
	}

	public String getSheetName() {
		return sheetName;
	}
 }
