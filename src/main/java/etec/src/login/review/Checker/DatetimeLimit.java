package etec.src.login.review.Checker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import etec.framework.security.encryption.service.EncryptionService;
import etec.framework.security.restriction.exception.PermissionDeniedException;
import etec.framework.security.restriction.interfaces.Reviewer;

/**過期
 * */
public class DatetimeLimit{

	private static final String LD = "MjAyNi8xMi8zMQ";
	
	private static final String FM = "eXl5eS9NTS9kZA";
	
	private static final String EM = "5qyK6ZmQ5bey6YGO5pyf77yM6KuL6IGv57Wh5bel56iL5bir";
	
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
