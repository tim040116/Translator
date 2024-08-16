package etec.framework.security.log.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import etec.common.factory.Params;

/**
 * @author Tim
 * @since 2023/02/24
 * @version dev
 * 	log
 * 	
 * */
public class Log {
	
	public static void info(Object content) {
		send(Params.log.COLOR_INFO,"INFO", content);
	}
	public static void abs(Object content) {
		SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		System.out.println(sfabs.format(new Date()) + " [\033[37;4m    \033[0m] : " + content);
	}
	public static void warn(Object content) {
		send(Params.log.COLOR_WARN,"WARN", content); 
	}

	public static void error(Exception e) {
		send(Params.log.COLOR_ERROR,"ERROR", e.getMessage());
		e.printStackTrace();
	}
	public static void debug(Object content) {
		send(Params.log.COLOR_DEBUG,"DEBUG",content);
	}
	public static void line() {
		System.out.println("------------------------------------------------------------------------------------------------");
	}
	private static void send(String color,String level, Object content) {
		
		if (Params.log.levelContains(level)) {
			String log = Params.log.sf.format(new Date()) 
					+ (Params.log.IS_COLOR?" [\033["+color+";4m" + level + "\033[0m] : ":" [" + level + "] : ") 
					+ content;
			System.out.println(log);
			//寫檔
			if(Params.log.IS_WRITE_FILE) {
				File newFile = new File(Params.log.LOG_FILE_NAME);
				newFile.getParentFile().mkdirs();
				try (
						FileWriter fw = new FileWriter(newFile, true);
						BufferedWriter bw = new BufferedWriter(fw);
					){
					if (!newFile.exists()) {
						newFile.createNewFile();
			        }
					bw.write(log+"\r\n");
					fw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
