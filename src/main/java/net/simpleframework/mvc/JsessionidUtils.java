package net.simpleframework.mvc;

import javax.servlet.http.HttpServletRequest;

import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class JsessionidUtils implements IMVCConst {

	private static final ThreadLocal<String> SESSIONIDs = new ThreadLocal<String>();

	public static String getId() {
		return SESSIONIDs.get();
	}

	public static void setJSessionId(final HttpServletRequest httpRequest) {
		String jsessionid = httpRequest.getParameter(JSESSIONID);
		if (!StringUtils.hasText(jsessionid)) {
			jsessionid = httpRequest.getSession().getId();
		}
		SESSIONIDs.set(jsessionid);
	}
}
