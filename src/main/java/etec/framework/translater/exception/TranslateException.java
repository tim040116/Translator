package etec.framework.translater.exception;

import java.io.IOException;

/**
 * <h1>傳換例外的父類別</h1>
 * <p></p>
 * <p></p>
 * 
 * <br>2024年2月17日	User	建立功能
 * 
 * @author	Tim
 * @since	1.0.0.0
 */
public class TranslateException extends IOException{

	public TranslateException(String script) {
		super(script);
	}

	private static final long serialVersionUID = 1L;

	
}
