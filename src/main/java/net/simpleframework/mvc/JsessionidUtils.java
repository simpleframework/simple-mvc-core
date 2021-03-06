package net.simpleframework.mvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class JsessionidUtils {
	private static final ThreadLocal<String> SESSIONIDs = new ThreadLocal<>();

	private static Map<String, String> PARAM_SESSIONIDs = new ConcurrentHashMap<>();

	public static String getId() {
		final String id = SESSIONIDs.get();
		if (id == null) {
			return null;
		} else {
			final String param = PARAM_SESSIONIDs.get(id);
			return param != null ? param : id;
		}
	}

	public static void setJSessionId(final String jsessionId) {
		SESSIONIDs.set(jsessionId);
	}

	public static void setJSessionId(final HttpServletRequest httpRequest) {
		final String jsessionId = httpRequest.getSession().getId();
		setJSessionId(jsessionId);

		// 当httpclient和当前不是一个session
		final String _jsessionId = httpRequest.getParameter(MVCConst.JSESSIONID);
		if (StringUtils.hasText(_jsessionId) && !_jsessionId.equals(jsessionId)) {
			PARAM_SESSIONIDs.put(jsessionId, _jsessionId);
		}
	}

	public static void remove(final String jsessionId) {
		PARAM_SESSIONIDs.remove(jsessionId);
		SESSIONIDs.remove();
	}
}
