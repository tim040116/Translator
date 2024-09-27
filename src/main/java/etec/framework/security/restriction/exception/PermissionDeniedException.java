package etec.framework.security.restriction.exception;

/**
 * 權限不足
 * */
public class PermissionDeniedException extends RuntimeException{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String message;

	public PermissionDeniedException(String msg) {
		super("權限不足："+msg);
		this.message = msg;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
