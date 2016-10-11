package net.simpleframework.mvc;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.simpleframework.common.Convert;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class SessionCache {

	private static final ISessionAttribute lSessionAttribute = new DefaultSessionAttribute(
			new ConcurrentHashMap<String, Object>());
	// 本地
	private static final SessionCache _lcache = new SessionCache(lSessionAttribute);

	// 缺省
	private static SessionCache _cache = new SessionCache(lSessionAttribute);

	public static void setCache(final SessionCache cache) {
		_cache = cache;
	}

	private final ISessionAttribute sAttribute;

	public SessionCache(final ISessionAttribute sAttribute) {
		this.sAttribute = sAttribute;
	}

	public static Object get(final Object key) {
		return _cache._get(key);
	}

	public static void put(final Object key, final Object value) {
		_cache._put(key, value);
	}

	public static Object remove(final Object key) {
		return _cache._remove(key);
	}

	public static Enumeration<String> getAttributeNames() {
		return _cache._getAttributeNames();
	}

	public static Object lget(final Object key) {
		return _lcache._get(key);
	}

	public static void lput(final Object key, final Object value) {
		_lcache._put(key, value);
	}

	public static Object lremove(final Object key) {
		return _lcache._remove(key);
	}

	public static Enumeration<String> lgetAttributeNames() {
		return _lcache._getAttributeNames();
	}

	public static class DefaultSessionAttribute implements ISessionAttribute {

		private final Map<String, Object> _attributes;

		public DefaultSessionAttribute(final Map<String, Object> _attributes) {
			this._attributes = _attributes;
		}

		@SuppressWarnings("unchecked")
		private Map<String, Object> getAttributes(final String sessionId) {
			if (sessionId == null) {
				return null;
			}
			Map<String, Object> attributes = (Map<String, Object>) _attributes.get(sessionId);
			if (attributes == null) {
				_attributes.put(sessionId, attributes = new ConcurrentHashMap<String, Object>());
			}
			return attributes;
		}

		@Override
		public void put(final String sessionId, final String key, final Object value) {
			Map<String, Object> attributes;
			if (key != null && (attributes = getAttributes(sessionId)) != null) {
				attributes.put(key, value);
			}
		}

		@Override
		public Object get(final String sessionId, final String key) {
			Map<String, Object> attributes;
			return key != null && (attributes = getAttributes(sessionId)) != null ? attributes.get(key)
					: null;
		}

		@Override
		public Object remove(final String sessionId, final String key) {
			Map<String, Object> attributes;
			return key != null && (attributes = getAttributes(sessionId)) != null
					? attributes.remove(key) : null;
		}

		@Override
		public void sessionDestroyed(final String sessionId) {
			if (sessionId != null) {
				_attributes.remove(sessionId);
				// System.out.println("sessionDestroyed[ISessionAttribute]: "
				// + sessionId);
			}
		}

		private final Enumeration<String> EMPTY_ENUM = new Enumeration<String>() {
			@Override
			public boolean hasMoreElements() {
				return false;
			}

			@Override
			public String nextElement() {
				return null;
			}
		};

		@Override
		public Enumeration<String> getAttributeNames(final String sessionId) {
			final Map<String, Object> attributes = getAttributes(sessionId);
			return attributes == null ? EMPTY_ENUM : Collections.enumeration(attributes.keySet());
		}
	}

	private String getJsessionId() {
		return JsessionidUtils.getId();
	}

	public ISessionAttribute getSessionAttribute() {
		return sAttribute;
	}

	private Object _get(final Object key) {
		return getSessionAttribute().get(getJsessionId(), Convert.toString(key));
	}

	private void _put(final Object key, final Object value) {
		getSessionAttribute().put(getJsessionId(), Convert.toString(key), value);
	}

	private Object _remove(final Object key) {
		return getSessionAttribute().remove(getJsessionId(), Convert.toString(key));
	}

	private Enumeration<String> _getAttributeNames() {
		return getSessionAttribute().getAttributeNames(getJsessionId());
	}

	public static HttpSessionListener SESSIONCACHE_LISTENER = new HttpSessionListener() {
		@Override
		public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		}

		@Override
		public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
			final String jsessionId = httpSessionEvent.getSession().getId();
			_cache.getSessionAttribute().sessionDestroyed(jsessionId);
			_lcache.getSessionAttribute().sessionDestroyed(jsessionId);
			JsessionidUtils.remove(jsessionId);
		}
	};
}
