package net.simpleframework.mvc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import net.simpleframework.mvc.AbstractUrlForward.RequestData;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class PageRequest extends HttpServletRequestWrapper {
	private RequestData requestData;

	private final Map parameters = new HashMap();

	private final Map headers = new HashMap();

	public PageRequest(final HttpServletRequest request) {
		super(request);
		// JsessionidUtils.setJSessionId(request);
		requestData = AbstractUrlForward.getRequestDataByRequest(request);
		if (requestData != null) {
			parameters.putAll(requestData.parameters);
			for (final Object object : requestData.attributes.entrySet()) {
				final Entry entry = (Entry) object;
				setAttribute((String) entry.getKey(), entry.getValue());
			}
			headers.putAll(requestData.headers);
			requestData = null;
		}

		// super优先级高
		parameters.putAll(super.getParameterMap());
	}

	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	private HttpSession httpSession;

	@Override
	public HttpSession getSession(final boolean create) {
		final HttpSession httpSession2 = super.getSession(create);
		if (httpSession2 == null) {
			return null;
		}
		if (httpSession == null || !httpSession.getId().equals(httpSession2.getId())) {
			httpSession = MVCContext.get().createHttpSession(httpSession2);
		}
		return httpSession;
	}

	@Override
	public int getIntHeader(final String name) {
		return Integer.parseInt(getHeader(name));
	}

	@Override
	public String getHeader(final String name) {
		final Enumeration<?> e = getHeaders(name);
		return (e != null && e.hasMoreElements()) ? (String) e.nextElement() : "";
	}

	@Override
	public Enumeration getHeaderNames() {
		final LinkedHashSet l = new LinkedHashSet();
		l.addAll(headers.keySet());
		l.addAll(Collections.list(super.getHeaderNames()));
		return Collections.enumeration(l);
	}

	@Override
	public Enumeration getHeaders(final String name) {
		// super优先级高
		final Enumeration<?> en = super.getHeaders(name);
		if (en != null && en.hasMoreElements()) {
			return en;
		} else {
			return (Enumeration<?>) headers.get(name);
		}
	}

	@Override
	public Map getParameterMap() {
		return parameters;
	}

	@Override
	public String getParameter(final String name) {
		final Object parameter = parameters.get(name);
		if (parameter instanceof String) {
			return (String) parameter;
		} else if (parameter instanceof String[]) {
			final String[] parameters = (String[]) parameter;
			if (parameters.length > 0) {
				return parameters[0];
			}
		}
		return super.getParameter(name);
	}

	@Override
	public Enumeration getParameterNames() {
		return new Vector(parameters.keySet()).elements();
	}

	@Override
	public String[] getParameterValues(final String name) {
		final Object objects = parameters.get(name);
		if (objects instanceof String[]) {
			return (String[]) objects;
		} else {
			return super.getParameterValues(name);
		}
	}
}
