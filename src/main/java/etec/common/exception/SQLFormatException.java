package etec.common.exception;

import java.io.IOException;
/**
 * @author	Tim
 * @since	2023年11月29日
 * 
 * 程式轉換時發生的問題
 */
public class SQLFormatException extends IOException{

	
	private static final long serialVersionUID = 1L;

	private String script = "";
	
	public SQLFormatException(String script) {
        super(script);
        this.script = script;
    }
	/**
	 * @author	Tim
	 * @since	2023年11月29日
	 * @param	functionName	出現錯誤的方法名稱
	 * @param	rightNumber		應該要有的參數數量
	 * @param	errNumber		實際取得的參數數量
	 * 
	 * 方法的參數數量錯誤
	 * */
	public static SQLFormatException wrongParam(String functionName,int rightNumber,int errNumber) {
		return new SQLFormatException("Wrong param number in function "+functionName
				+" : This function need "+rightNumber+" params "
				+" but you have "+errNumber+" params ");
	}
	/**
	 * @author	Tim
	 * @since	2023年11月29日
	 * @param	keyWord	關鍵字
	 * 
	 * 缺少關鍵字
	 * */
	public static SQLFormatException missingKeyWord(String keyWord) {
		return new SQLFormatException("Can not find the keyword : "+keyWord);
	}
	
	public String getErrorScript() {
		return script;
	}

}
