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
	 * <h1>以逗號為條件切分</h1>
	 * <p>會trim資料</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月26日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	context	要切分的字串
	 * @throws	
	 * @see
	 * @return	List<String>	切分後的清單
			 */
	public static List<String> splitComma(String context) {
		List<String> lst = new ArrayList<String>();
		String res = context.replaceAll(",|\\(|\\)", Mark.MAHJONG_RED+"$0"+Mark.MAHJONG_RED);
		int cntBracket = 0;
		String temp = "";
		for(String str : res.split(Mark.MAHJONG_RED)) {
			cntBracket += "(".equals(str)?1:")".equals(str)?-1:0;
			if(",".equals(str)&&cntBracket==0) {
				lst.add(temp.trim());
				temp = "";
			}else {
				temp += str;
			}
		}
		return lst;
	}
	/**
	 * <h1>以逗號為條件切分</h1>
	 * <p>會trim資料</p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月26日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	context	要切分的字串
	 * @throws	
	 * @see
	 * @return	List<String>	切分後的清單
			 */
	public static List<String> splitComma(String context,Function<String,String> f) {
		List<String> lst = new ArrayList<String>();
		String res = context.replaceAll(",|\\(|\\)", Mark.MAHJONG_RED+"$0"+Mark.MAHJONG_RED);
		int cntBracket = 0;
		String temp = "";
		for(String str : res.split(Mark.MAHJONG_RED)) {
			cntBracket += "(".equals(str)?1:")".equals(str)?-1:0;
			if(",".equals(str)&&cntBracket==0) {
				lst.add(f.apply(temp.trim()));
				temp = "";
			}else {
				temp += str;
			}
		}
		return lst;
	}
	
}
