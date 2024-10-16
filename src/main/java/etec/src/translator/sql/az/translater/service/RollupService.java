package etec.src.translator.sql.az.translater.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollupService {
	/**
	 * <h1>rollup</h1>
	 * <p></p>
	 * <p></p>
	 *
	 * <h2>異動紀錄</h2>
	 * <br>2024年7月31日	Tim	建立功能
	 *
	 * @author	Tim
	 * @since	1.0.0.0
	 * @param	enclosing_method_arguments
	 * @throws	e
	 * @see
	 * @return	return_type
			 */
	public static String changeRollUp(String sql) {
		String res = sql;
		RollupModel model = new RollupModel();
		//整理語法
		res = res.replaceAll("(?i),\\s*ROLLUP\\s*\\(\\s*",",ROLLUP\\(");
		StringBuffer sb = new StringBuffer();
		String reg = "(?is)(?<head>^.*\\n(?<tab>[ \t]*))(?<groupby>GROUP\\s+BY\\s+(?:ROLLUP\\s*\\([^)]+\\)|[\\w.]+)(?:\\s*,\\s*(?:ROLLUP\\s*\\([^)]+\\)|[\\w.]+))+)(?<foot>.*)";
		Matcher m = Pattern.compile(reg).matcher(res);
		while(m.find()) {
			if(!m.group(0).toUpperCase().contains("ROLLUP")) {
				m.appendReplacement(sb, Matcher.quoteReplacement(m.group(0)));
				continue;
			}
			//轉換程式以註解的方式保留
			String remark = m.group("groupby").replaceAll("\\s+"," ");
			//新的GROUP BY
			String newStr = "";

			//處裡欄位
			String allcol = m.group("groupby").replaceAll("(?i)GROUP\\s+BY\\s+", "");
			List<String> lstCol = new ArrayList<>();
			List<String> lstRollup = new ArrayList<>();
			Matcher mcol = Pattern.compile("(?i)ROLLUP\\s*\\(([^)]+)\\)|([\\w.]+)").matcher(allcol);
			while(mcol.find()) {
				if(mcol.group(1)!=null)
					{lstRollup.add(mcol.group(0));}
				else{lstCol.add(mcol.group(0));}
			}
			model.sql = m.group();
			model.head = m.group("head");
			model.tab = m.group("tab");
			model.lstCol = lstCol;
			model.lstRollup = lstRollup;
			model.lstRollup.replaceAll(e -> e.replaceAll("(?i)ROLLUP\\s*\\(([^)]+)\\)","$1"));
			model.foot = m.group("foot");
			model.remark = remark;
			//超過一個rollup要拆union
			newStr = (lstRollup.size()==1)?newStr = singleRollup(model):multiRollup(model);
			m.appendReplacement(sb, Matcher.quoteReplacement(newStr));
		}
		m.appendTail(sb);
		res = sb.toString();
		return res;
	}
	//單一rollup轉換
	public static String singleRollup(RollupModel m) {
		RollupModel model = m;
		List<String> lstAll = new ArrayList<>();
		lstAll.addAll(model.lstCol);
		lstAll.addAll(model.lstRollup);
		String groupby = "\r\n" + model.tab + "GROUP BY ROLLUP("+String.join(",", lstAll)+")";
		String having  = "\r\n"+model.tab+"HAVING " + String.join(" IS NOT NULL\r\n"+model.tab+"   AND ", model.lstCol)+" IS NOT NULL\r\n"+model.tab;
		String res = model.head
				+ "\r\n" + model.tab + "-- " + model.remark
				+ groupby
				+ having
				+ model.foot
			;
		return res;
	}
	//將複數rollup拆分成union
	public static String multiRollup(RollupModel m) {
		RollupModel model = m;
		String res = "";
		if(model.lstRollup.size()==1) {
			res = singleRollup(model);
		}else if(model.lstRollup.size()==2) {
			String[] arrRollup = model.lstRollup.get(1).split("\\s*,\\s*");
			model.lstRollup.remove(1);
			res = singleRollup(model);
			for(String rollup : arrRollup) {
				model.lstCol.add(rollup);
				String[] arrNewHeader = model.head.split("(?i)FROM",2);
				model.head = arrNewHeader[0]
						.replaceAll("(?i)\\Q"+rollup+"\\E","NULL")
						+"FROM"
						+ arrNewHeader[1];
				res = singleRollup(model)+"\r\n\tUNION\r\n\t"+res;
			}
		}else{
//			try {
				String[] arrRollup = model.lstRollup.get(1).split("\\s*,\\s*");
				model.lstRollup.remove(1);
				res = multiRollup(model);
				for(String rollup : arrRollup) {
					model.lstCol.add(rollup);
					res = multiRollup(model)+"\r\n\tUNION\r\n\t"+res;
				}
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
		}
		return res;
	}
}

class RollupModel{
	String sql;
	String remark;
	String head;
	String tab;
	List<String> lstCol;
	List<String>lstRollup;
	String foot;
}
