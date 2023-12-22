package etec.sql.gp.translater;

import java.util.ArrayList;
import java.util.List;

import etec.common.utils.ConvertFunctionsSafely;
import etec.common.utils.TransduceTool;

/**
 * @author	Tim
 * @since	2023年12月19日
 * @version	4.0.0.0
 * 
 * <h1>查詢語法轉換</h1>
 * 
 * <br>此類別並不是用來轉換SQL裡的語法
 * <br>若需要轉換語法請使用
 * <br>{@link etec.sql.gp.translater.SQLTranslater}
 * <br>
 * <br>此類別著重於SQL語句的拆解，
 * <br>負責將sub query,join,union,with...等
 * <br>複合型語句拆解成單純的查詢語句
 * */
public class DQLTranslater {
	/**
	 * @author	Tim
	 * @since	2023年12月19日
	 * 
	 * <h1>QUALIFY ROW_NUMBER()語法轉換</h1>
	 * 
	 * */
	public String changeQualifaRank(String sql) {
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
		//1.UNION
		
		String[] arrSplitType = {//所有特殊階段的關鍵字
			 "FROM"
			,"((OUTER|INNER|LEFT|RIGHT|CROSS)\\s+)?JOIN"
			,"UNION"
			,"WHERE"
			,"GROUP\\s+BY"
			,"ORDER\\s+BY"
			,"QUALIFY\\s+ROW_NUMBER\\(\\)"
		};
		String regKey = "(?i)"+arrSplitType[0];
		for(int i = 1; i<arrSplitType.length;i++) {
			regKey+="|"+arrSplitType[i];
		}
		res = res.replaceAll(regKey, TransduceTool.SPLIT_CHAR_BLACK+"$0");
		String[] arrSplitStr = res.split(TransduceTool.SPLIT_CHAR_BLACK);
		
		//依照特徵分裝
		String select;
		
		for(String str : arrSplitStr) {
			
		}
		return res;
	}


}
