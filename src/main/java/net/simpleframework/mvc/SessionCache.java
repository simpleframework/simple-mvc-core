package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
	// 缺省
	public static SessionCache DEFAULT = new SessionCache(new DefaultSessionAttribute());

	public static Object lget(final Object key) {
		return DEFAULT.get(key);
	}

	public static void lput(final Object key, final Object value) {
		DEFAULT.put(key, value);
	}

	public static Object lremove(final Object key) {
		return DEFAULT.remove(key);
	}

	private final ISessionAttribute sAttribute;

	public SessionCache(final ISessionAttribute sAttribute) {
		this.sAttribute = sAttribute;
		all.add(this);
	}

	public Object get(final Object key) {
		return sAttribute.get(getJsessionId(), Convert.toString(key));
	}

	public void put(final Object key, final Object value) {
		sAttribute.put(getJsessionId(), Convert.toString(key), value);
	}

	public Object remove(final Object key) {
		return sAttribute.remove(getJsessionId(), Convert.toString(key));
	}

	public Enumeration<String> getAttributeNames() {
		return sAttribute.getAttributeNames(getJsessionId());
	}

	private String getJsessionId() {
		return JsessionidUtils.getId();
	}

	public static class DefaultSessionAttribute implements ISessionAttribute {

		private final Map<String, Map<String, Object>> _attributes = new ConcurrentHashMap<String, Map<String, Object>>();

		protected Map<String, Object> getAttributes(final String sessionId) {
			if (sessionId == null) {
				return null;
			}
			Map<String, Object> attributes = _attributes.get(sessionId);
			if (attributes == null) {
				_attributes.put(sessionId, attributes = new HashMap<String, Object>());
			}
			return attributes;
		}

		@Override
		public Object get(final String sessionId, final String key) {
			Map<String, Object> attributes;
			return key != null && (attributes = getAttributes(sessionId)) != null ? attributes.get(key)
					: null;
		}

		@Override
		public void put(final String sessionId, final String key, final Object value) {
			Map<String, Object> attributes;
			if (key != null && (attributes = getAttributes(sessionId)) != null) {
				attributes.put(key, value);
			}
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
			}
		}

		@Override
		public Enumeration<String> getAttributeNames(final String sessionId) {
			final Map<String, Object> attributes = getAttributes(sessionId);
			return attributes == null ? EMPTY_ENUM : Collections.enumeration(attributes.keySet());
		}
	}

	private static final Enumeration<String> EMPTY_ENUM = new Enumeration<String>() {
		@Override
		public boolean hasMoreElements() {
			return false;
		}

		@Override
		public String nextElement() {
			return null;
		}
	};

	public static HttpSessionListener SESSIONCACHE_LISTENER = new HttpSessionListener() {
		@Override
		public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		}

		@Override
		public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
			final String jsessionId = httpSessionEvent.getSession().getId();
			for (final SessionCache cache : all) {
				cache.sAttribute.sessionDestroyed(jsessionId);
			}
			JsessionidUtils.remove(jsessionId);
		}
	};

	private static List<SessionCache> all = new ArrayList<SessionCache>();

	// private static Log log = LogFactory.getLogger(SessionCache.class);
}
