package etec.common.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * <h1>做程式的映射處理</h1>
 * <p></p>
 * <h2>屬性</h2>
 * <p></p>
 * <h2>方法</h2>
 * <p></p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年3月5日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class ClassTool {

//	public static List<Class> getClassFromPackage(String pack){
//		boolean recursive = true ;//是否查詢子包
//		String packageName = "";
//		String packageDirName = "";
//		Enumeration<URL> dirs;
//		
//		try {
//			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
//			while(dirs.hasMoreElements()) {
//				URL url = dirs.nextElement();
//				String protocol = url.getProtocol();
//				if("file".equals(protocol)) {
//					String filePath = url.getFile();
////					findClass
//					
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	}
	
//	https://blog.51cto.com/u_15344989/5012608
	
	public static void findClassInPackageByFile(String packageName,String filePath,final boolean recursive,List<Class> clazzs) {
		File dir = new File(filePath);
		if(!dir.exists()||!dir.isDirectory()) {
			return;
		}
		
		//在目錄下找到所有檔案並進行條件過濾
		File[] dirFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				boolean acceptDir = recursive && file.isDirectory();
				boolean acceptClass = file.getName().endsWith(".class");
				return acceptDir||acceptClass;
			}
			
		});
		
		for(File file : dirFiles) {
			if(file.isDirectory()) {
				
			}
		}
	}
}
