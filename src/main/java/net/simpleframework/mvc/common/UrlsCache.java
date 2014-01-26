package net.simpleframework.mvc.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class UrlsCache extends ObjectEx {

	public String getUrl(final PageParameter pp, final Class<? extends AbstractMVCPage> mClass) {
		return getUrl(pp, mClass, null);
	}

	public String getUrl(final PageParameter pp, final Class<? extends AbstractMVCPage> mClass,
			final String params) {
		return AbstractMVCPage.url(getPageClass(mClass.getName()), params);
	}

	public Class<? extends AbstractMVCPage> getPageClass(final String key) {
		return classMapping.get(key);
	}

	protected void put(final Class<? extends AbstractMVCPage> val) {
		put(val, val);
	}

	protected void put(final Class<? extends AbstractMVCPage> key,
			final Class<? extends AbstractMVCPage> val) {
		classMapping.put(key.getName(), val);
	}

	protected void put(final String key, final Class<? extends AbstractMVCPage> val) {
		classMapping.put(key, val);
	}

	private final Map<String, Class<? extends AbstractMVCPage>> classMapping;
	{
		classMapping = new ConcurrentHashMap<String, Class<? extends AbstractMVCPage>>();
	}
}
