package etec.common.utils.convert_safely;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import etec.common.utils.Mark;

/**
 * <h1>安全的切分</h1>
 * <p></p>
 * <h2>屬性</h2>
 * <p></p>
 * <h2>方法</h2>
 * <p></p>
 * 
 * <h2>異動紀錄</h2>
 * <br>2024年2月26日	Tim	建立功能
 * 
 * @author	Tim
 * @version	4.0.0.0
 * @since	4.0.0.0
 * @see		
 */
public class SplitCommaSafely {
	
	/**
	 * @author	Tim
	 * @since	2023年11月30日
	 * 	
	 * 會依小括號進行分層
	 * 避免函式轉換時造成錯位
	 * */
	public static List<String> savelyConvert(String script,Function<String, String> function) {
		List<String> lst = new ArrayList<String>();
		String res = script.replaceAll(",|\\(|\\)", Mark.MAHJONG_RED+"$0"+Mark.MAHJONG_RED);
		int cntBracket = 0;
		String temp = "";
		for(String str : res.split(Mark.MAHJONG_RED)) {
			cntBracket += "(".equals(str)?1:")".equals(str)?-1:0;
			if(",".equals(str)&&cntBracket==0) {
				lst.add(temp);
				temp = "";
			}else {
				temp += str;
			}
		}
		return lst;
	}
	
}
