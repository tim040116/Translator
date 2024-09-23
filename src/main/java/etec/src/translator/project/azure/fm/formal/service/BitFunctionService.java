package etec.src.translator.project.azure.fm.formal.service;

import etec.framework.context.convert_safely.service.ConvertFunctionsSafely;

public class BitFunctionService {

	/**
	 * 轉換bit_and bit_or bit_extract
	 * 只需要加上dev.就好了
	 * @since	2024/09/06
	 * */
	public static String replaceBitAnd(String script) {
		return script.replaceAll("(?i)(?<!dev\\.)\\bbit_(?:and|or|extract)\\s*\\(","dev.$0");
	}
	
	/**
	 * bit_gen_aggt跟bit_or_aggt
	 * ,dev.bit_gen_aggt(STRING_AGG(cast(ostore_bit_seq as VARCHAR(MAX)),',')  WITHIN GROUP (ORDER BY ostore_bit_seq ASC))  
	 * 
	 * bit_or_aggt需要加工，但程式無法處理
	 * 
	 * @since	2024/09/06
	 * */
	public static String replaceBitGenAggt(String script) {
		String res = script;
		res = res.replaceAll("\\s*(bit_gen_aggt|bit_or_aggt)\\s*\\(", "$1\\(");
		ConvertFunctionsSafely cfs = new ConvertFunctionsSafely();
		res = cfs.savelyConvert(res,(t)->{
			t = t.replaceAll("(?i)(?<!dev\\.|-- )\\b(bit_gen_aggt|bit_or_aggt)\\s*\\(([^()]+)\\)","\r\n-- $0\r\ndev.bit_gen_aggt\\(STRING_AGG\\(cast\\($2 as VARCHAR\\(MAX\\)\\),','\\)  WITHIN GROUP \\(ORDER BY $2 ASC\\)\\)");
			return t;
		});
		return res;
	}
}
