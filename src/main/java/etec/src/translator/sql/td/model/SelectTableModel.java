package etec.src.translator.sql.td.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.context.convert_safely.model.Mark;
import etec.framework.context.convert_safely.service.ConvertFunctionsSafely;
import etec.framework.context.translater.exception.UnknowSQLTypeException;

/**
 * <h1>將Select部分拆分</h1>
 * <p></p>
 * <p></p>
 * <h2>屬性</h2>
 * <p>construction : 為資料結構</p>
 * <p></p>
 * <h2>方法</h2>
 * <p></p>
 * <p></p>
 * <h2>建構函式</h2>
 * <p>()	創建空的物件</p>
 * <p>(String script)	會自動將語法拆解，分裝</p>
 * <h2>異動紀錄</h2>
 * <br>2024年2月20日	Tim	建立功能
 * 
 * @author	Tim
 * @since	4.0.0.0
 * @see		
 */
public class SelectTableModel {
	
	/**
	 * <h1>所有特殊階段的關鍵字</h1>
	 * <p></p>
	 * @author	Tim
	 * @since	4.0.0.0
	 */
	private static final String REG_KEY = "(?i)"
		+ "SEL(?:ECT)?"
		+ "|WITH"
		+ "|FROM"
		+ "|((OUTER|INNER|LEFT|RIGHT|CROSS)\\s+)?JOIN"
		+ "|WHERE"
		+ "|GROUP\\s+BY"
		+ "|ORDER\\s+BY"
		+ "|QUALIFY\\s+ROW_NUMBER"
	;

	/**
	 * <h1>SQL語法結構</h1>
	 * <p>在分段後會依照此結構進行組裝</p>
	 * @author	Tim
	 * @since	4.0.0.0
	 */
	public String construction = "";
	
	public String select = "";
	
	public String from = "";
	
//	private List<String> WITH = new ArrayList<String>();

	public Map<String,String> join = new HashMap<String,String>();
	
	public String where = "";
	
	public String groupBy = "";
	
	public String orderBy = "";
	
	public String qualifyRowNumber = "";
	
	public SelectTableModel(){}
	
	/**
	 * <h1>Teradata 的 Select 語法物件</h1>
	 * <p>直接把select語法導入，</p>
	 * <p>會在建置時直接拆解分奘語法</p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月21日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	
	 * @throws	
	 * @see
	 * @return	
			 */
	public SelectTableModel(String sql) throws UnknowSQLTypeException{
		//安插標記
		sql = sql
				.replaceAll(REG_KEY, Mark.MAHJONG_BLACK+"$0")
				.replaceAll("(?i)(TRIM\\s*\\([\\S\\s]*?)"+Mark.MAHJONG_BLACK+"(FROM)", "$1$2")//排除TRIM(FROM)
				.replaceAll("\\s+"+Mark.MAHJONG_BLACK, Mark.MAHJONG_BLACK+"$0");//保留段落間的空白
		;
		//分裝
		int cntJoin = 1;
		Pattern p = Pattern.compile("[^"+Mark.MAHJONG_BLACK+"]+");
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			String str = m.group(0);
			//分辨斷落的類型
			if(str.matches("\\s*")) {
				m.appendReplacement(sb,str);
				continue;
			}else if(str.matches("(?i)SEL(?:ECT)?[\\S\\s]+")) {
				select = str;
				m.appendReplacement(sb,getTag("SELECT")+str);
			}else if(str.matches("(?i)FROM[\\S\\s]+")) {
				from = str;
				m.appendReplacement(sb,getTag("FROM"));
			}else if(str.matches("(?i)((OUTER|INNER|LEFT|RIGHT|CROSS)\\\\s+)?JOIN[\\S\\s]+")) {
				String joinid = "JOIN_"+cntJoin;
				cntJoin++;
				join.put(joinid,str);
				m.appendReplacement(sb,getTag(joinid));
			}else if(str.matches("(?i)WHERE[\\S\\s]+")) {
				where = str;
				m.appendReplacement(sb,getTag("WHERE"));
			}else if(str.matches("(?i)GROUP\\s+BY[\\S\\s]+")) {
				groupBy = str;
				m.appendReplacement(sb,getTag("GROUP_BY"));
			}else if(str.matches("(?i)ORDER\\s+BY[\\S\\s]+")) {
				orderBy = str;
				m.appendReplacement(sb,getTag("ORDER_BY"));
			}else if(str.matches("(?i)\\bQUALIFY\\s+ROW_NUMBER[\\S\\s]+")) {
				qualifyRowNumber = ConvertFunctionsSafely.decodeMark(str);
				m.appendReplacement(sb,getTag("QUALIFY_ROW_NUMBER"));
			}else {
				throw new UnknowSQLTypeException(str, null);
			}
		}
		m.appendTail(sb);
		construction = sb.toString().replaceAll(Mark.MAHJONG_BLACK, "");
	}
	
	/**
	 * <h1>組成取代文字</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月20日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	id	取代文字的ID
	 * @throws	
	 * @see
	 * @return	取代的文字
			 */
	private String getTag(String id) {
		return "<SelectTableModel_construction_"+id+">";
	}
	
	/**
	 * <h1>toString會直接將物件還原成SQL語句</h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月20日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	
	 * @throws	
	 * @see
	 * @return	
			 */
	@Override
	public String toString() {
		String res = construction
				.replace(getTag("SELECT"),select)
				.replace(getTag("FROM"),from)
				.replace(getTag("WHERE"),where)
				.replace(getTag("GROUP_BY"),groupBy)
				.replace(getTag("ORDER_BY"),orderBy)
				.replace(getTag("QUALIFY_ROW_NUMBER"),qualifyRowNumber)
		;
		for(Entry<String, String> en : join.entrySet()) {
			res = res.replace(getTag(en.getKey()),en.getValue());
		}
		return res;
	}
	
	/**
	 * <h1></h1>
	 * <p></p>
	 * <p></p>
	 * 
	 * <h2>異動紀錄</h2>
	 * <br>2024年2月26日	Tim	建立功能
	 * 
	 * @author	Tim
	 * @since	4.0.0.0
	 * @param	
	 * @throws	
	 * @see		
	 * @return	
			 */
	public void changeConstruction() {
		
	}
}
