package etec.common.exception.sql;

import etec.common.exception.TransduceException;

/**
 * <h1>SQL語法轉換的父類別</h1>
 * <p></p>
 * <p></p>
 * 
 * <br>2024年2月17日	User	建立功能
 * 
 * @author	Tim
 * @since	1.0.0.0
 * @param
 * @throws
 * @exception
 * @see
 * @return
		 */
public class SQLTransduceException extends TransduceException{

	public SQLTransduceException(String script) {
		super(script);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
