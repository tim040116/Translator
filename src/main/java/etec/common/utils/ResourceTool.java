package etec.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 讀取靜態資源
 * 
 * @author Tim
 * @since 2023/04/19
 * @version 2.dev
 * 
 */
public class ResourceTool {

	/**讀取檔案
	 * @author	Tim
	 * @since	2.dev
	 * @return	String
	 * @throws	IOException,URISyntaxException
	 * */
	public String readFile(String file) throws IOException, URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resourceUrl = classLoader.getResource(file);
		File resourceDir = new File(resourceUrl.toURI());
		String res = FileTool.readFile(resourceDir);
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
