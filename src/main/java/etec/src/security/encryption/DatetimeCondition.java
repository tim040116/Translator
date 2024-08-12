package etec.src.security.encryption;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import etec.framework.permission.exception.PermissionDeniedException;
import etec.framework.permission.interfaces.Condition;
import etec.framework.permission.service.EncryptionService;

public class DatetimeCondition implements Condition {

	private static final String LD = "MjAyNi8xMi8zMQ";
	
	private static final String FM = "eXl5eS9NTS9kZA";
	
	private static final String EM = "5qyK6ZmQ5bey6YGO5pyf77yM6KuL6IGv57Wh5bel56iL5bir";
	
	@Override
	public int check(String[] args) throws PermissionDeniedException {
		try {
			if((new Date()).after((new SimpleDateFormat(EncryptionService.decodeA(FM))).parse(EncryptionService.decodeA(LD)))) {
				throw new PermissionDeniedException(EncryptionService.decodeB(EM));
			}
		} catch (ParseException e) {
			throw new PermissionDeniedException(EncryptionService.decodeB(EM));
		}
		return 0;
	}

}
