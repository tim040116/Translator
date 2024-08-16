package etec.framework.file.params.exception;

public class MissParamsException extends ParamException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public MissParamsException(Object key) {
        super("參數遺失，請確認參數檔是否完整: "+key);
    }
}
