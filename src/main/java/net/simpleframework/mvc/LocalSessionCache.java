package net.simpleframework.mvc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.Convert;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class LocalSessionCache {
	static LocalSessionAttribute lsAttribute = new LocalSessionAttribute();

	public static void put(final Object key, final Object value) {
		lsAttribute.put(JsessionidUtils.getId(), Convert.toString(key), value);
	}

	public static Object get(final Object key) {
		return lsAttribute.get(JsessionidUtils.getId(), Convert.toString(key));
	}

	public static Object remove(final Object key) {
		return lsAttribute.remove(JsessionidUtils.getId(), Convert.toString(key));
	}

	public static class LocalSessionAttribute implements ISessionAttribute {
		final Map<String, Map<String, Object>> sObjects = new ConcurrentHashMap<String, Map<String, Object>>();

		private Map<String, Object> getAttributes(final String sessionId) {
			Map<String, Object> attributes = sObjects.get(sessionId);
			if (attributes == null) {
				sObjects.put(sessionId, attributes = new ConcurrentHashMap<String, Object>());
			}
			return attributes;
		}

		@Override
		public void put(final String sessionId, final String key, final Object value) {
			if (key != null) {
				getAttributes(sessionId).put(key, value);
			}
		}

		@Override
		public Object get(final String sessionId, final String key) {
			return key == null ? null : getAttributes(sessionId).get(key);
		}

		@Override
		public Object remove(final String sessionId, final String key) {
			return key == null ? null : getAttributes(sessionId).remove(key);
		}

		@Override
		public void sessionDestroyed(final String sessionId) {
			sObjects.remove(sessionId);
		}

		@Override
		public Enumeration<String> getAttributeNames(final String sessionId) {
			return Collections.enumeration(getAttributes(sessionId).keySet());
		}
	}
}
