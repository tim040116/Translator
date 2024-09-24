package etec.framework.file.params.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import main.Main;

/**
 * 讀取靜態資源
 * 
 * @author Tim
 * @since 2023/04/19
 * @version 2.dev
 * 
 */
public class ResourceTool {

	/**讀取內部參數檔案
	 * @author	Tim
	 * @since	2.dev
	 * @return	String
	 * @throws	IOException,URISyntaxException
	 * */
	public static String readFile(String file){
		String res = "";
		InputStream in = Main.class.getResourceAsStream("/META-INF/"+file);
		try (
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
		) {
			while(br.ready()) {
				String line = br.readLine();
				res += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	/** 取得所有yml檔
	 * @author	Tim
	 * @since	2.dev
	 * @return	Map<String, File>
	 * @throws	IOException,URISyntaxException
	 * */
	public Map<String, File> readYml() throws IOException, URISyntaxException {
		Map<String, File> ymlMap = new HashMap<String, File>();
		ymlMap.putAll(readYml("META-INF"));
		return ymlMap;
	}

	/**for readYml()
	 * */
	private Map<String, File> readYml(String path) throws IOException, URISyntaxException {
		Map<String, File> ymlMap = new HashMap<String, File>();
		ClassLoader classLoader = getClass().getClassLoader();
		URL resourceUrl = classLoader.getResource(path);
		File resourceDir = new File(resourceUrl.toURI());

		if (resourceDir.isDirectory()) {
			File[] files = resourceDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					ymlMap.putAll(readYml(path + "\\" + file.getName()));
				} else {
					ymlMap.put(file.getName(), file);
				}
			}
		}
		return ymlMap;
	}
}
