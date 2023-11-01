package etec.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import etec.main.ParamsFactory;

public class ParamsTool {
	
	public static Map<String,File> readConfigFile() {
		Map<String,File> mapParams = new HashMap<String,File>();
		for(String fn : ParamsFactory.CONFIG_FILE_LIST) {
			File f = new File(ParamsFactory.ROOT_CONFIG_PATH+fn+".txt");
			mapParams.put(fn, f);
		}
		return mapParams;
	}
	

	/** 
	 * @author	Tim
	 * @since	2023年07月14日
	 * 23-07-14	Tim
	 * 		read params in VMINFO_var.txt
	 * 		but only used in etecManagerMail
	 * 23-10-03	Tim	新增註解功能以雙減號為註解
	 * 
	 */
	public static Map<String,String> readParam(String sp,File f) {
		Log.abs("Reading config file: "+f.getName());
		Map<String,String> mapParams = new ParamMap<String,String>();
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
