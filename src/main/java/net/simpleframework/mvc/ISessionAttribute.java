package net.simpleframework.mvc;

import java.util.Enumeration;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface ISessionAttribute {

	/**
	 * @param sessionId
	 * @param key
	 * @param value
	 */
	void put(String sessionId, String key, Object value);

	/**
	 * @param sessionId
	 * @param key
	 * @return
	 */
	Object get(String sessionId, String key);

	/**
	 * @param sessionId
	 * @param key
	 * @return
	 */
	Object remove(String sessionId, String key);

	/**
	 * 获取所有属性的名称
	 * 
	 * @param sessionId
	 * @return
	 */
	Enumeration<String> getAttributeNames(String sessionId);

	/**
	 * 当session销毁时触发
	 * 
	 * @param sessionId
	 */
	void sessionDestroyed(String sessionId);
}
