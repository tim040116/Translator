package etec.src.security.login.review;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import etec.framework.file.readfile.service.FileTool;
import etec.framework.security.encryption.factory.EncryptionFactory;
import etec.framework.security.restriction.exception.PermissionDeniedException;
import etec.framework.security.restriction.interfaces.Reviewer;

public class LoginReviewer  implements Reviewer {
	

	@Override
	public int check(Map<String,String> args) throws PermissionDeniedException {
		try {
			Map<String,String> mapfile = readSecFile();
			if(
				(new Date()).after(new SimpleDateFormat(mapfile.get("fmd")).parse(mapfile.get("md")))
			) {
				return -1;
			}
			else if(!mapfile.get("fmi").replace("i",mapfile.get("ei")).replace("p",mapfile.get("eps")).equals(EncryptionFactory.base64.decode(mapfile.get("hash")))||!mapfile.get("fmi").replace("i",args.get("id")).replace("p",args.get("pass")).equals(EncryptionFactory.base64.decode(mapfile.get("hash")))) {
				return -2;
			}
			else {
				return 1;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private Map<String,String> readSecFile() throws IOException {
		Map<String,String> res = new HashMap<String,String>();
		String str = FileTool.readFile("sec.txt").trim();
		String args = EncryptionFactory.base64.decode(str);
		
		String reg = "(?i)([^-_]+)_([^-_]+)";
		Matcher m = Pattern.compile(reg).matcher(args);
		while (m.find()) {
			res.put(m.group(1), m.group(2));
		}
		return res;
	}

}
