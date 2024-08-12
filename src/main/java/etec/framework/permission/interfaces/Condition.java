package etec.framework.permission.interfaces;

import etec.framework.permission.exception.PermissionDeniedException;

/**權限條件*/
@etec.framework.permission.annotation.Condition
public interface Condition {

	//return 0 為通過
	public int check(String[] args) throws PermissionDeniedException;
	
}
