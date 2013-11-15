package net.simpleframework.mvc;

import net.simpleframework.common.th.RuntimeExceptionEx;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCException extends RuntimeExceptionEx {
	private static final long serialVersionUID = 7838430650458772788L;

	public MVCException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static RuntimeException of(final Throwable throwable) {
		return _of(MVCException.class, null, throwable);
	}
}
