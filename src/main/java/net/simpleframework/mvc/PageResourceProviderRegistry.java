package net.simpleframework.mvc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageResourceProviderRegistry extends ObjectEx {

	public static PageResourceProviderRegistry get() {
		return singleton(PageResourceProviderRegistry.class);
	}

	private IPageResourceProvider defaultProvider;

	private final Map<String, IPageResourceProvider> providers;
	{
		providers = new ConcurrentHashMap<>();
	}

	public PageResourceProviderRegistry() {
	}

	public IPageResourceProvider getPageResourceProvider(final String name) {
		IPageResourceProvider provider;
		if (StringUtils.hasText(name) && (provider = providers.get(name)) != null) {
			return provider;
		}
		if (defaultProvider == null) {
			registered(defaultProvider = MVCContext.get().getDefaultPageResourceProvider());
		}
		return defaultProvider;
	}

	public void registered(final IPageResourceProvider pageResourceProvider) {
		final String name = pageResourceProvider.getName();
		if (StringUtils.hasText(name)) {
			providers.put(name, pageResourceProvider);
		}
	}
}
