package etec.framework.permission.exception;

/**
 * 權限不足
 * */
public class PermissionDeniedException extends Exception{
	
	private String message;
	
	public PermissionDeniedException(String msg) {
		super("權限不足："+msg);
		this.message = msg;
	}

	public String getMessage() {
		return message;
	}

}
