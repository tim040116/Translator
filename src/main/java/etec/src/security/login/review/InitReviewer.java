package etec.src.security.login.review;

import java.util.Map;

import etec.framework.security.restriction.exception.PermissionDeniedException;
import etec.framework.security.restriction.interfaces.Reviewer;

public class InitReviewer  implements Reviewer {

	@Override
	public int check(Map<String, String> args) throws PermissionDeniedException {
		return 0;
	}

}
