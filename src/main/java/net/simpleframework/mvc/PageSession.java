package net.simpleframework.mvc;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionContext;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings("deprecation")
public class PageSession implements HttpSession {

	private final MVCEventAdapter adapter = MVCContext.get().getEventAdapter();

	private final HttpSession httpSession;

	public PageSession(final HttpSession httpSession) {
		this.httpSession = httpSession;
		if (JsessionidUtils.getId() == null) {
			JsessionidUtils.setJSessionId(httpSession.getId());
		}
	}

	public HttpSession getHttpSession() {
		return httpSession;
	}

	@Override
	public String getId() {
		return httpSession.getId();
	}

	@Override
	public Object getAttribute(final String key) {
		return SessionCache.get(key);
	}

	@Override
	public void setAttribute(final String key, final Object val) {
		SessionCache.put(key, val);
		adapter.attributeAdded(new HttpSessionBindingEvent(httpSession, key, val));
	}

	@Override
	public void removeAttribute(final String key) {
		final Object val = SessionCache.remove(key);
		adapter.attributeRemoved(new HttpSessionBindingEvent(httpSession, key, val));
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return SessionCache.getAttributeNames();
	}

	@Override
	public void invalidate() {
		httpSession.invalidate();
	}

	@Override
	public boolean isNew() {
		return httpSession.isNew();
	}

	@Override
	public long getCreationTime() {
		return httpSession.getCreationTime();
	}

	@Override
	public long getLastAccessedTime() {
		return httpSession.getLastAccessedTime();
	}

	@Override
	public int getMaxInactiveInterval() {
		return httpSession.getMaxInactiveInterval();
	}

	@Override
	public void setMaxInactiveInterval(final int interval) {
		httpSession.setMaxInactiveInterval(interval);
	}

	@Override
	public ServletContext getServletContext() {
		return httpSession.getServletContext();
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return httpSession.getSessionContext();
	}

	@Override
	public Object getValue(final String key) {
		return httpSession.getValue(key);
	}

	@Override
	public void putValue(final String key, final Object val) {
		httpSession.putValue(key, val);
	}

	@Override
	public void removeValue(final String key) {
		httpSession.removeValue(key);
	}

	@Override
	public String[] getValueNames() {
		return httpSession.getValueNames();
	}
}
