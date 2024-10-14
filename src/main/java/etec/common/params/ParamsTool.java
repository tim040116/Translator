package etec.common.params;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import etec.common.factory.Params;
import etec.framework.file.params.model.ParamMap;

/***/
public class ParamsTool {

	/**
	 * <h1>依照根目錄讀取外部參數</h1>
	 *
	 * <p>請放在同一個根目錄
	 * <p>附檔名請一致
	 *
	 * @author	Tim
	 * @since	dev
	 * @param	rootPath	參數檔根目錄
	 * @param	fileType	附檔名
	 * @return	參數檔檔名,檔案
	 * @throws	IOException
	 * */
	public static Map<String,File> readConfigFile(String rootPath,String fileType) throws IOException {
		Map<String,File> mapParams = new HashMap<>();
		
		for(String fn : Params.CONFIG_FILE_LIST) {
			File f = new File("config\\"+fn+".txt");
			mapParams.put(fn, f);
		}
		return mapParams;
	}


	/**
	 * <h1>讀取外部的參數檔的功能</h1>
	 *
	 * @author	Tim
	 * @since	2023年07月14日
	 * <br> 2023/10/03	Tim	新增註解功能以雙減號為註解
	 *
	 */
	public static Map<String,String> readParam(String sp,File f) {
		Map<String,String> mapParams = new ParamMap<>();
		try(
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis,Charset.forName("utf-8"));
			BufferedReader br = new BufferedReader(isr);
		){
			while (br.ready()) {
				String line = br.readLine();
				line = line.replaceAll("\\-\\-.*", "").trim();
				String[] arrp = line.split(sp);
				int len = arrp.length;
				String key = "";
				String val = "";
				if(len==0) {
					continue;
				}else if(len==1) {
					key = arrp[0];
				}else {
					key = arrp[0];
					val = arrp[1];
				}
				mapParams.put(key,val);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mapParams;
	}
}
