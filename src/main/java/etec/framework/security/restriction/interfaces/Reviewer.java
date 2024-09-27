package etec.framework.security.restriction.interfaces;

import java.util.Map;

import etec.framework.security.restriction.exception.PermissionDeniedException;

/**權限條件*/
@etec.framework.security.restriction.annotation.Condition
public interface Reviewer {

	//return 0 為通過
	public int check(Map<String,String> args) throws PermissionDeniedException;

}
