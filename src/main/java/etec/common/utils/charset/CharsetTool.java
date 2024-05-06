package etec.common.utils.charset;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import etec.common.utils.log.Log;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

/**
 * <h1>文檔編碼的處理</h1>
 * 
 * <h2>屬性</h2>
 * <p></p>
 * <h2>方法</h2>
 * <p>getCharset 			: 取得編碼</p>
 * <p>readFileInCharset 	: 依編碼讀檔</p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年5月6日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		info.monitorenter.cpdetector.io.CodepageDetectorProxy
 * @see		org.apache.commons.io.FileUtils
 */
public class CharsetTool {
	
	/**
	 * <h1>檢查文字檔案編碼</h1>
	 * <p>
	 * <br>需要 cpdetector 1.0.10
	 * <br>
	 * </p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月6日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	
	 * @throws	
	 * @see	info.monitorenter.cpdetector.io.CodepageDetectorProxy
	 * @return	return_type
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static Charset getCharset(String filePath) throws MalformedURLException, IOException {
		CodepageDetectorProxy dtr = CodepageDetectorProxy.getInstance();
		dtr.add(new ParsingDetector(false));
		dtr.add(JChardetFacade.getInstance());
		dtr.add(ASCIIDetector.getInstance());
		dtr.add(UnicodeDetector.getInstance());
		Charset chr = null;
		chr = dtr.detectCodepage(Paths.get(filePath).toUri().toURL());
		return chr;
	}
	
	/**
	 * <h1>依照指定編碼讀檔</h1>
	 * <p>若編碼不同則轉為設定的編碼</p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年5月6日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	targetCharset	想要的編碼
	 * @param	filePath		檔案路徑
	 * @throws	IOException
	 * @see		org.apache.commons.io.FileUtils
	 * @return	String
	 */
	public static String readFileInCharset(String targetCharset,String filePath) throws MalformedURLException, IOException {
		Charset chsFile = getCharset(filePath);
		Charset chsNew  = Charset.forName(targetCharset);
		File file = new File(filePath);
		String content = "";
		//取得原編碼字串
		content = FileUtils.readFileToString(file,chsFile);
		//判斷是否需要轉換
		if(!chsNew.equals(chsFile)) {
			Log.debug("非正常編碼：" + chsFile.name()+" 將進行轉換");
			//轉成新編碼
			content = new String(content.getBytes(),chsNew);
		}
		return content;
	}
}
