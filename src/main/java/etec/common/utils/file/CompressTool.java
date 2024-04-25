package etec.common.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h1>壓縮工具</h1>
 * <p></p>
 * <h2>屬性</h2>
 * 	<br>static String {@link #}
 * <h2>方法</h2>
 * 	<br>static String {@link #compressFolderToZip}
 * <h2>異動紀錄</h2>
 * <br>2024年4月25日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class CompressTool {
	
	/**
	 * <h1>將檔案壓縮成zip</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月25日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	sourceFolder	來源檔案的路徑
	 * @param	rootPathInZip	包成zip時壓縮檔裡面先有一層資料夾
	 * @param	targetFileName	zip檔路徑及檔名
	 * @throws	IOException		
	 * @see		
	 * @return	void
	 */
	public static void compressFolderToZip(String sourceFolder,String rootPathInZip,String targetFileName) throws IOException {
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(targetFileName))) {
            // 压缩文件夹
            compressFolder(sourceFolder,rootPathInZip, zipOutputStream);
        } catch (IOException e) {
            throw e;
        }
	}
	
	/**
	 * <h1>判斷為檔案或資料夾</h1>
	 * <p>
	 * <br>檔案會進行壓縮{@link #addToZipFile}
	 * <br>資料夾則進入子目錄
	 * </p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月25日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	sourceFolder	來源檔案的路徑
	 * @param	folderName		包成zip時壓縮檔裡面先有一層資料夾
	 * @param	zipOutputStream	zip壓縮檔流
	 * @throws	IOException		
	 * @see		compressFolderToZip
	 * @return	void
	 */
	private static void compressFolder(String sourceFolder, String folderName, ZipOutputStream zipOutputStream) throws IOException {
		File folder = new File(sourceFolder);
	        File[] files = folder.listFiles();
	        
	        if (files != null) {
	            for (File file : files) {
	                if (file.isDirectory()) {
	                    // 压缩子文件夹
	                    compressFolder(file.getAbsolutePath(), folderName + "/" + file.getName(), zipOutputStream);
	                } else {
	                    // 压缩文件
	                    addToZipFile(folderName + "/" + file.getName(), file.getAbsolutePath(), zipOutputStream);
	                }
	            }
	        }
	    }
	
	/**
	 * <h1>將檔案壓縮成zip</h1>
	 * <p>將檔案壓成Zip</p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年4月25日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	sourceFolder	來源檔案的路徑
	 * @param	rootPathInZip	包成zip時壓縮檔裡面先有一層資料夾
	 * @param	targetFileName	zip壓縮檔流
	 * @throws	IOException		
	 * @see		
	 * @return	void
	 */  
    private static void addToZipFile(String fileName, String fileAbsolutePath, ZipOutputStream zipOutputStream) throws IOException {
	    	// 创建ZipEntry对象并设置文件名
	        ZipEntry entry = new ZipEntry(fileName);
	        zipOutputStream.putNextEntry(entry);
	        // 读取文件内容并写入Zip文件
	        try (FileInputStream fileInputStream = new FileInputStream(fileAbsolutePath)) {
	            byte[] buffer = new byte[1024];
	            int bytesRead;
	            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
	                zipOutputStream.write(buffer, 0, bytesRead);
	            }
	        }
	        // 完成当前文件的压缩
	        zipOutputStream.closeEntry();
	    }
	    
}
