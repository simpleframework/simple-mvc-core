package net.simpleframework.mvc.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.mvc.AbstractMVCPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class UrlsCache extends ObjectEx {

	public Class<? extends AbstractMVCPage> getUrl(final String key) {
		return urls.get(key);
	}

	protected final Map<String, Class<? extends AbstractMVCPage>> urls = new ConcurrentHashMap<String, Class<? extends AbstractMVCPage>>();
}
