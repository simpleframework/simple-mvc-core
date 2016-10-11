package net.simpleframework.mvc;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import net.simpleframework.common.Convert;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class SessionCache {

	private static final ISessionAttribute lSessionAttribute = new DefaultSessionAttribute();
	// 本地
	private static final SessionCache _lcache = new SessionCache(lSessionAttribute);

	// 缺省
	private static SessionCache _cache = new SessionCache(lSessionAttribute);

	public static void setCache(final SessionCache cache) {
		_cache = cache;
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

	public static class JedisSessionAttribute implements ISessionAttribute {
		private final int expire = 3600;

		private final JedisPool pool;

		public JedisSessionAttribute(final JedisPool pool) {
			this.pool = pool;
		}

		@Override
		public Object get(final String sessionId, final String key) {
			Jedis jedis = null;
			try {
				jedis = pool.getResource();
				return IoUtils.deserialize(jedis.hget(sessionId.getBytes(), key.getBytes()));
			} catch (final Exception e) {
				log.warn(e);
				return null;
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		@Override
		public void put(final String sessionId, final String key, final Object value) {
			Jedis jedis = null;
			try {
				jedis = pool.getResource();
				final byte[] sbytes = sessionId.getBytes();
				if (!jedis.exists(sbytes)) {
					jedis.expire(sbytes, expire);
				}
				jedis.hset(sbytes, key.getBytes(), IoUtils.serialize(value));
			} catch (final IOException e) {
				log.warn(e);
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		@Override
		public Object remove(final String sessionId, final String key) {
			Jedis jedis = null;
			try {
				jedis = pool.getResource();
				return jedis.hdel(sessionId.getBytes(), key.getBytes());
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		@Override
		public Enumeration<String> getAttributeNames(final String sessionId) {
			Jedis jedis = null;
			try {
				jedis = pool.getResource();
				final Iterator<byte[]> it = jedis.hkeys(sessionId.getBytes()).iterator();
				return new Enumeration<String>() {
					@Override
					public boolean hasMoreElements() {
						return it.hasNext();
					}

					@Override
					public String nextElement() {
						return new String(it.next());
					}
				};
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		@Override
		public void sessionDestroyed(final String sessionId) {
			Jedis jedis = null;
			try {
				jedis = pool.getResource();
				jedis.del(sessionId.getBytes());
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
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

	private final ISessionAttribute sAttribute;

	public SessionCache(final ISessionAttribute sAttribute) {
		this.sAttribute = sAttribute;
	}

	public ISessionAttribute getSessionAttribute() {
		return sAttribute;
	}

	private String getJsessionId() {
		return JsessionidUtils.getId();
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

	private static Log log = LogFactory.getLogger(SessionCache.class);
}
