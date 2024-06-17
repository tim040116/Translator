package etec.common.exception;

/**
 * <h1>缺少註解</h1>
 * <p>
 * <br>缺少所需的註解
 * </p>
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
public class MissingAnnotationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MissingAnnotationException(Class<?> c) {
		super("Missing annotation : "+c.getSimpleName()+" ,please add this annotation in your script "+c.getCanonicalName());
	}
	
}
