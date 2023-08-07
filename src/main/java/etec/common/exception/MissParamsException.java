package etec.common.exception;

public class MissParamsException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public MissParamsException(Object key) {
        super("Can't find param: "+key);
    }
}
