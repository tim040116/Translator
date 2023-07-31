package etec.common.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Tim
 * @since 2023/02/24
 * @version dev
 * 	log
 * 	
 * */
public class Log {

	private static SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

	private static String LOG_LEVEL = "DEBUG";
	
	public static boolean IS_COLOR = false;
	
	private static List<String> arlv = new ArrayList<String>();
	static {
		switch (LOG_LEVEL) {
			case "DEBUG":
				arlv.add("DEBUG");
			case "INFO":
				arlv.add("INFO");
			case "WARN":
				arlv.add("WARN");
			case "ERROR":
				arlv.add("ERROR");
			default:
				break;
		}
	}

	public static void info(Object content) {
		send("INFO", content);
	}

	public static void warn(Object content) {
		send("33","WARN", content);
	}

	public static void error(Object content) {
		send("31","ERROR", content);
	}

	public static void debug(Object content) {
		send("DEBUG",content);
	}
	public static void line() {
		System.out.println("------------------------------------------------------------------------------------------------");
	}
	// 固定格式LOG
	private static void send(String level, Object content) {
		if (arlv.contains(level)) {
			System.out.println(sdFormat.format(new Date()) + " [" + level + "] : " + content);
		}
	}
	private static void send(String color,String level, Object content) {
		if(!IS_COLOR) {
			send(level,content);
		}
		else if (arlv.contains(level)) {
			System.out.println(sdFormat.format(new Date()) + " [\033["+color+";4m" + level + "\033[0m] : " + content);
		}
	}
}
