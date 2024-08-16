package etec.framework.security.restriction.interfaces;

import etec.framework.security.restriction.exception.PermissionDeniedException;

/**權限條件*/
@etec.framework.security.restriction.annotation.Condition
public interface Reviewer {

	//return 0 為通過
	public int check(String[] args) throws PermissionDeniedException;
	
}
