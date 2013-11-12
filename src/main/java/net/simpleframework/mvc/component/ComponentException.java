package net.simpleframework.mvc.component;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.common.th.RuntimeExceptionEx;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ComponentException extends RuntimeExceptionEx {

	public ComponentException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public static RuntimeException of(final Throwable cause) {
		return _of(ComponentException.class, null, cause);
	}

	public static ComponentException of(final String message) {
		return _of(ComponentException.class, message);
	}

	public static ComponentException wrapException_ComponentRef(final String ref) {
		return new ComponentException($m("ComponentException.0", ref), null);
	}

	private static final long serialVersionUID = 5936937563262430751L;
}
