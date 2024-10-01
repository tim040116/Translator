package etec.src.security.project.login.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import etec.common.model.VersionModel;
import etec.framework.security.encryption.model.HashResult;
import etec.framework.security.restriction.exception.PermissionDeniedException;
import etec.framework.security.restriction.interfaces.Reviewer;

public class CheckLoginService  implements Reviewer {


	@Override
	public int check(Map<String,String> args) throws PermissionDeniedException {
		try {
			HashResult m = CreateKeyFileService.decode();
			String hash = CreateKeyFileService.getHash(m.getFm(),args.get("id"), args.get("pass"), VersionModel.VERSION);
			if((new Date()).after(m.getLimitDate())){return -1;}
			else if(!hash.equals(m.getHash())){
				return -2;}
			else if(!VersionModel.VERSION.equals(m.getVersion())){return -3;}
			else if((new Date()).before(VersionModel.VERSION_DATE)){return -4;}

			else{return 1;}
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}



}
