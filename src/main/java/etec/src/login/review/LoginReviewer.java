package etec.src.login.review;

import etec.framework.security.restriction.exception.PermissionDeniedException;
import etec.framework.security.restriction.interfaces.Reviewer;

public class LoginReviewer  implements Reviewer {

	@Override
	public int check(String[] args) throws PermissionDeniedException {
		return 0;
	}

}
