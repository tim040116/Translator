package etec.common.utils.encoding;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map.Entry;

/**
 * <h1>中文編碼處理</h1>
 * <p></p>
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #}
 * <h2>異動紀錄</h2>
 * <br>2024年4月9日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class EncodingTool {
	
	/**
	 * <h1>取得字串的編碼</h1>
	 * <p>
	 * <br>取得編碼的Charset
	 * </p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月9日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	str	要檢驗的字串
	 * @throws	UnsupportedEncodingException	不支援此編碼
	 * @see		
	 * @return	Charset
			 */
	public static Charset getEncoding(String str) throws UnsupportedEncodingException {
		for(Entry<String, Charset> e: Charset.availableCharsets().entrySet()) {
			if(str.equals(new String(str.getBytes(e.getKey()),e.getKey()))) {
				return e.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * <h1>轉換編碼</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月9日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	str	字串
	 * @param	charset	編碼
	 * @throws	
	 * @see		
	 * @return	String	
			 */
	public static String convert(String str,Charset charset) {
		return new String(str.getBytes(), charset);
	}
}
