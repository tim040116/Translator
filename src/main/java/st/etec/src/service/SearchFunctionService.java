package st.etec.src.service;

import java.util.ArrayList;
import java.util.List;

public class SearchFunctionService {
	// 將btq轉成sql
	public List<String> subQuery(String dqlcontent) {
			List<String> lstDQLTable = new ArrayList<String>();
			String type = "";
			String temp = "";
			int cntSub = 0;
			String[] arr = dqlcontent.split("\\s+");
			for(String str : arr) {
				if(";".equals(str)) {
					type = "";
					temp = "";
					cntSub = 0;
					continue;
				}
				//子查詢
				if(cntSub > 0) {
					cntSub += "(".equals(str)?1:")".equals(str)?-1:0;
					if(cntSub == 0) {
						SearchFunctionService sfs = new SearchFunctionService();
						lstDQLTable.addAll(sfs.subQuery(temp));
						temp = "";
					}else {
						temp+=" "+str;
					}
					continue;
				}
				//table
				switch (type) {
					case ""://起頭
						if("FROM".equals(str)||"JOIN".equals(str)) {
							type = str;
							continue;
						}
						break;
					case "JOIN"://join
						if("(".equals(str)) {
							cntSub = 1;
							type = "";
							continue;
						}else {
							lstDQLTable.add(str);
							type = "";
						}
						break;
					case "FROM"://from的table name
						type = "FROM2";
						if("(".equals(str)) {
							cntSub = 1;
							continue;
						}else {
							lstDQLTable.add(str);
						}
						break;
					case "FROM2"://table name的後面
						type = "JOIN".equals(str)?"JOIN":"FROM3";
						break;
					case "FROM3"://判斷from 的類型
						type = ",".equals(str)?"FROM":"JOIN".equals(str)?"JOIN":"";
						break;
					default:
						break;
				}
				
			}
			return lstDQLTable;
		}
}
