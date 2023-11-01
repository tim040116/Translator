package etec.common.exception;

import etec.common.enums.SQLTypeEnum;

/**
 * @author	Tim
 * @since	2023年10月20日
 * 當遇到無法識別的sql語句會回傳此例外
 * */
public class UnknowSQLTypeException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String script = "";
	
	public UnknowSQLTypeException(String script,SQLTypeEnum type) {
        super("Unknow sql type : "+script);
        this.script = script;
    }

	public String getErrorScript() {
		return script;
	}
	
	
}
