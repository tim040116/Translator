package etec.app.params;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import etec.common.utils.param.ParamsTool;

public class FamilyMartParams {
	
	/**
	 * @author	Tim
	 * @since	2023年10月3日
	 * Perl檔的參數要不要置換
	 * */
	public final boolean IS_REPLACE_PARAMS_IN_PERL;
	/**
	 * @author	Tim
	 * @since	2024年08月14日
	 * MERGE INTO要不要置換
	 * */
	public final boolean MERGE_INTO;
	/**
	 * @author	Tim
	 * @since	2023年10月3日
	 * 產檔時的編碼
	 * */
	public final Charset WRITE_FILE_CHARSET;
	
	/**
	 * @author	Tim
	 * @since	2023年10月3日
	 * 編碼是big5的檔案類型清單，不在清單中則是utf-8
	 * */
	public final List<String> LIST_FILE_TYPE_BIG5;
	
	public FamilyMartParams(File f) {
		Map<String, String> map = ParamsTool.readParam("=",f);
		this.IS_REPLACE_PARAMS_IN_PERL = "TRUE".equals(map.get("IS_REPLACE_PARAMS_IN_PERL").toUpperCase());
		this.WRITE_FILE_CHARSET = Charset.forName(map.get("WRITE_FILE_CHARSET"));
		this.LIST_FILE_TYPE_BIG5 = Arrays.asList(map.get("LIST_FILE_TYPE_BIG5").split(","));
		this.MERGE_INTO = "TRUE".equals(map.get("IS_REPLACE_PARAMS_IN_PERL").toUpperCase());

		
	}
}
