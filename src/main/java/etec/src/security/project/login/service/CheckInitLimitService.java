package etec.src.security.project.login.service;

import java.util.Map;

import etec.framework.security.restriction.exception.PermissionDeniedException;
import etec.framework.security.restriction.interfaces.Reviewer;

public class CheckInitLimitService  implements Reviewer {

	@Override
	public int check(Map<String, String> args) throws PermissionDeniedException {
		return 0;
	}

}
