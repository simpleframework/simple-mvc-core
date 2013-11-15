package net.simpleframework.mvc;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.simpleframework.common.Convert;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class SessionCache {
	private static ISessionAttribute sAttribute;

	public static void registSessionAttribute(final ISessionAttribute sAttribute) {
		SessionCache.sAttribute = sAttribute;
	}

	private static ISessionAttribute getSessionAttribute() {
		if (sAttribute == null) {
			sAttribute = LocalSessionCache.lsAttribute;
		}
		return sAttribute;
	}

	public static HttpSessionListener sessionListener = new HttpSessionListener() {

		@Override
		public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		}

		@Override
		public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
			final String sessionId = httpSessionEvent.getSession().getId();
			// System.out.println("remove objects from session: " + sessionId);
			LocalSessionCache.lsAttribute.sessionDestroyed(sessionId);
			final ISessionAttribute sAttribute = getSessionAttribute();
			if (sAttribute != LocalSessionCache.lsAttribute) {
				sAttribute.sessionDestroyed(sessionId);
			}
		}
	};

	public static void put(final Object key, final Serializable value) {
		getSessionAttribute().put(JsessionidUtils.getId(), Convert.toString(key), value);
	}

	public static Object get(final Object key) {
		return getSessionAttribute().get(JsessionidUtils.getId(), Convert.toString(key));
	}

	public static Object remove(final Object key) {
		return getSessionAttribute().remove(JsessionidUtils.getId(), Convert.toString(key));
	}

	public static Enumeration<String> getAttributeNames() {
		return getSessionAttribute().getAttributeNames(JsessionidUtils.getId());
	}
}
