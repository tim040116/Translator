package etec.src.security.project.login.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.common.model.VersionModel;
import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.encryption.factory.EncryptionFactory;
import etec.framework.security.encryption.model.HashResult;

public class CreateKeyFileService {

	private static final String SEC_FILE = "sec.txt";//金鑰檔名
	private static final String QTE = "|";//key value的分格
	private static final String DTR = "#";//data的分隔

	/**
	 * 檢核項目
	 * */
	public static String print(String id,String pass,String limitDate) {
		//base
		
		String format = "yyyy/mm/dd";
		String fm = "i!=gks/ap-v";
		String vv = EncryptionFactory.base64.encode(VersionModel.VERSION);
		//
		String fmd = EncryptionFactory.base64.encode(format);
		String md = EncryptionFactory.base64.encode(limitDate);
		String hash = getHash(fm,id,pass,VersionModel.VERSION);
		String key = EncryptionFactory.base64.encode(VersionModel.ALL_LOG);
		String key2 = EncryptionFactory.base64.encode(key+key+fm);
		//
		String res =
			        "fm"   + QTE + fm
			+ DTR + "eps"  + QTE + "!Q2w3e4r5t"
			+ DTR + "fmd"  + QTE + fmd
			+ DTR + "primaryHashKey2024"  + QTE + key2
			+ DTR + "ei"   + QTE + "admin"
			+ DTR + "md"   + QTE + md
			+ DTR + "vv"   + QTE + vv
			+ DTR + "vss"   + QTE + "12"
			+ DTR + "epkv"  + QTE + "!QAZ@WSX3edc4rfv"
			+ DTR + "hash" + QTE + hash
			+ DTR + "key"  + QTE + key
		;
		res = res.replaceAll("=", "\r");
		res = EncryptionFactory.base64.encode(res);
		res = EncryptionFactory.base64.encode(res);
		return res;
	}


	public static HashResult decode() throws IOException, ParseException {
		Map<String,String> map = readSecFile();
		HashResult m = new HashResult();
		m.setFm(map.get("fm"));
		m.setHash(map.get("hash"));
		m.setVersion(EncryptionFactory.base64.decode(map.get("vv")));
		m.setLimitDate(new SimpleDateFormat(EncryptionFactory.base64.decode(map.get("fmd"))).parse(EncryptionFactory.base64.decode(map.get("md"))));
		return m;
	}

	public static String getHash(String key,String id,String pass,String version) {
		String res = key
			.replace("k", EncryptionFactory.base64.encode(key))
			.replace("i", EncryptionFactory.base64.encode(id))
			.replace("p", EncryptionFactory.base64.encode(pass))
			.replace("v", EncryptionFactory.base64.encode(version))
		;
		return EncryptionFactory.base64.encode(res);
	}

	public static  Map<String,String> readSecFile() throws IOException {
		Map<String,String> res = new HashMap<>();
		String str = FileTool.readFile(SEC_FILE).trim();
		String args = EncryptionFactory.base64.decode(str);
		args = EncryptionFactory.base64.decode(args).replaceAll("\r","=");
		String reg = "(?i)([^"+QTE+DTR+"]+)\\"+QTE+"([^"+QTE+DTR+"]+)";
		Matcher m = Pattern.compile(reg).matcher(args);
		while (m.find()) {
			res.put(m.group(1), m.group(2));
		}
		return res;
	}
}


