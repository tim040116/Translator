package etec.common.utils;

import java.util.HashMap;

import etec.common.exception.MissParamsException;

public class ParamMap<K,V> extends HashMap<K,V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public V get(Object key) throws MissParamsException{
		if(super.get(key)==null) {
			throw new MissParamsException(key);
		}
        return super.get(key);
    }
}
