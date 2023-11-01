package etec.src.transducer;

/**
 * @author	Tim
 * @since	2023年11月1日
 * @version	3.3.1.1
 * 
 * 處理 SF SP 用到的不是單段語法
 * 
 * 
 * */
public class SFTransducer {

	/**
	 * @author	Tim
	 * @since	2023年11月1日
	 * IF ELSEIF ELSE的語法轉換
	 * 
	 * */
	public static String transduceIF(String script) {
		String res = script.toUpperCase();
		//先處理else 因為會跟case的搞混
		res = res
				.replaceAll("END\\s+IF\\s*;","中")
				.replaceAll("(\\s+)ELSE(\\s+)\\b([^中]+)\\b中", "$2END$1ELSE$2BEGIN$2$3$2END\r\n")
				.replaceAll("中","END IF;")
				;
				//if else if end if
		res = res
				.replaceAll("\\bEND\\s+IF\\s*;","END")
				.replaceAll("\\bIF\\s+(.*)\\sTHEN","IF $1 BEGIN")
				.replaceAll("\\bELSEIF\\s+(.*)THEN","END\r\nELSE IF $1 BEGIN")
				;
		return res;
	}
}
