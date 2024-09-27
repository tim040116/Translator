package etec.framework.file.excel_maker.model;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import etec.common.exception.MissingAnnotationException;
import etec.framework.file.excel_maker.annotation.Key;
import etec.framework.file.excel_maker.annotation.SheetModel;

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

	public Worksheet() throws MissingAnnotationException, IllegalArgumentException, IllegalAccessException{
		//取得類別
		@SuppressWarnings("unchecked")
		Class<Object> ct = (Class<Object>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		//確認是否有註解
		if(!ct.getClass().isAnnotationPresent(SheetModel.class)) {
			throw new MissingAnnotationException(SheetModel.class);
		}
		//取得活頁簿名稱
		this.sheetName = ct.getAnnotation(SheetModel.class).sheetName();
		//取得欄位
		for(Field fd : ct.getFields()) {
			if(key==null) {
				if(fd.isAnnotationPresent(Key.class)) {
					key = fd.getName();
					String a = null;
					fd.get(a);
				}
			}
		}
	}

	public String getSheetName() {
		return sheetName;
	}
 }
