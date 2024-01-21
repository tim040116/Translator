package etec.src.sql.gp.translater;

import java.util.ArrayList;
import java.util.List;

import etec.common.enums.SelectAreaEnum;
import etec.common.exception.UnknowSQLTypeException;
import etec.common.utils.Mark;
import etec.common.utils.convert_safely.ConvertFunctionsSafely;

/**
 * @author	Tim
 * @since	2023年12月19日
 * @version	4.0.0.0
 * 
 * <h1>查詢語法轉換</h1>
 * 
 * <br>此類別並不是用來轉換SQL裡的語法
 * <br>若需要轉換語法請使用
 * <br>{@link etec.src.sql.gp.translater.SQLTranslater}
 * <br>
 * <br>此類別著重於SQL語句的拆解，
 * <br>負責將sub query,join,union,with...等
 * <br>複合型語句拆解成單純的查詢語句
 * */
public class DQLTranslater {
	
	/**
	 * @author	Tim
	 * @throws UnknowSQLTypeException 
	 * @since	2023年12月26日
	 * 
	 * <h1>統整DQL的所有轉換</h1>
	 * 
	 * <br>qualify row_number over 處理
	 * <br>
	 * 
	 * */
	public String easyReplace(String script) throws UnknowSQLTypeException {
		String res = script;
		res = changeQualifaRank(res);
		return res;
	}
	
	
	/**
	 * @author	Tim
	 * @throws UnknowSQLTypeException 
	 * @since	2023年12月19日
	 * 
	 * <h1>QUALIFY ROW_NUMBER()語法轉換</h1>
	 * 
	 * */
	public String changeQualifaRank(String sql) throws UnknowSQLTypeException {
		String res = "";
		String temp = sql;
		/*
		 *  這個語法是建立在沒有sub query的前提下
		 *  所以要先處理子查詢
		 *  
		 * 第一步要先將UNION語法分段 
		 * 再把一些階段的關鍵字加上標記
		 *再用split切分 
		 * 
		 * */
		//安插標記
		String[] arrSplitType = {//所有特殊階段的關鍵字
			"SELECT"
			,"WITH"
			,"FROM"
//			,"((OUTER|INNER|LEFT|RIGHT|CROSS)\\s+)?JOIN"
//			,"UNION"
			,"WHERE"
//			,"GROUP\\s+BY"
//			,"ORDER\\s+BY"
			,"QUALIFY\\s+ROW_NUMBER\\(\\)"
		};
		String regKey = "(?i)"+arrSplitType[0];
		for(int i = 1; i<arrSplitType.length;i++) {
			regKey+="|"+arrSplitType[i];
		}
		temp = temp
				.replaceAll(regKey, Mark.MAHJONG_BLACK+"$0")
				.replaceAll("(?i)(TRIM\\s*\\([\\S\\s]*?)"+Mark.MAHJONG_BLACK+"(FROM)", "$1$2")//排除TRIM(FROM)
		;
		String[] arrSplitStr = temp.split(Mark.MAHJONG_BLACK);
		//依照特徵分裝
		String select=""
				,from=""
				,where=""
//				,groupBy=""
//				,orderBy=""
				,rowNumber="";
		List<String> lstWith = new ArrayList<String>();
//		List<String> lstJoin = new ArrayList<String>();
		List<SelectAreaEnum> lstArea = new ArrayList<SelectAreaEnum>();//紀錄各階段的順序
		for(String str : arrSplitStr) {
			if(str.matches("\\s*")) {
				res+=str;
				continue;
			}else if(str.matches("(?i)WITH[\\S\\s]+")) {
				lstWith.add(str);
				lstArea.add(SelectAreaEnum.WITH);
			}else if(str.matches("(?i)SELECT[\\S\\s]+")) {
				select = str;
				lstArea.add(SelectAreaEnum.SELECT);
			}else if(str.matches("(?i)FROM[\\S\\s]+")) {
				from = str;
				lstArea.add(SelectAreaEnum.SELECT);
//			}else if(str.matches("(?i)((OUTER|INNER|LEFT|RIGHT|CROSS)\\\\s+)?JOIN[\\S\\s]+")) {
//				lstJoin.add(str);
//				lstArea.add(SelectAreaEnum.JOIN);
			}else if(str.matches("(?i)WHERE[\\S\\s]+")) {
				where = str;
				lstArea.add(SelectAreaEnum.WHERE);
//			}else if(str.matches("(?i)GROUP\\s+BY[\\S\\s]+")) {
//				groupBy = str;
//				lstArea.add(SelectAreaEnum.GROUP_BY);
//			}else if(str.matches("(?i)ORDER\\s+BY[\\S\\s]+")) {
//				orderBy = str;
//				lstArea.add(SelectAreaEnum.ORDER_BY);
			}else if(str.matches("(?i)QUALIFY\\s+ROW_NUMBER\\(\\)[\\S\\s]+")) {
				rowNumber = str;
				lstArea.add(SelectAreaEnum.QUALFY_ROW_NUMBER);
			}else {
				throw new UnknowSQLTypeException(str, null);
			}
		}
		/* 
		 * 轉換邏輯
		 * 	- select
		 * 		1.有邏輯的部分全部只留Alias name
		 * 	- from
		 * 		1.包sub query
		 * 		2.加上row_number
		 * 	- where
		 * 		1.加上where row_number = 1
		 * 
		 * 2023/12/26 Tim 跟Jason討論過，可以將邏輯放在子查詢
		 * */
		//select只留Alias name
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		String newSelect = select.replaceAll("(?i)(SELECT(\\s+DISTINCT)?\\s+)[\\S\\s]+", "$1")+
			cfs.savelyConvert(select.replaceAll("(?i)SELECT(\\s+DISTINCT)?\\s+", ""), (t) ->{
				return t
					.replaceAll("(?i)[^,]+\\s+AS\\s+([^\\s,]+)", "$1")//只保留Alias name
					.replaceAll("\\S+\\.(\\S+)","tmp_qrn.$1")// 清除Table Alias name
				;
			});
		String newFrom = "FROM ( "
			+select.replaceAll("(?i)\\s+DISTINCT", "")
			+"\t,"+rowNumber.replaceAll("(?i)QUALIFY\\s+", "").replaceAll("([\\S\\s]*\\))[^\\)]+$", "$1")+" AS ROW_NUMBER\r\n\t"
			+from+where+" ) tmp_qrn \r\n where tmp_qrn.ROW_NUMBER "
			+rowNumber.replaceAll("[\\S\\s]*\\)([^\\)]+)$", "$1")
		;
		res += newSelect+newFrom;
		return res;
	}


}
