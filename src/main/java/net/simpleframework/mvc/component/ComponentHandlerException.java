package net.simpleframework.mvc.component;

import net.simpleframework.common.th.RuntimeExceptionEx;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ComponentHandlerException extends RuntimeExceptionEx {
	private static final long serialVersionUID = 7623116181965540895L;

	public ComponentHandlerException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static ComponentHandlerException of(final String msg) {
		return _of(ComponentHandlerException.class, msg);
	}

	public static RuntimeException of(final Throwable throwable) {
		return _of(ComponentHandlerException.class, null, throwable);
	}
}
