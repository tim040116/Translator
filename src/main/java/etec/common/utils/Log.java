package etec.common.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import etec.main.Params;

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
		SimpleDateFormat sfabs = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");
		System.out.println(sfabs.format(new Date()) + " [\033[37;4m    \033[0m] : " + content);
	}
	public static void warn(Object content) {
		send(Params.log.COLOR_WARN,"WARN", content); 
	}

	public static void error(Object content) {
		send(Params.log.COLOR_ERROR,"ERROR", content);
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
			if(Params.log.IS_WRITE_FILE) {
				try {
					FileTool.addFile(Params.log.LOG_FILE_NAME, log);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
