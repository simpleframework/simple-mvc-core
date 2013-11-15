package net.simpleframework.mvc.component;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.mvc.MVCUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public final class ComponentRegistryFactory extends ObjectEx {
	private final static String PROTOCAL_FILE_PREFIX = "file:///";

	public static ComponentRegistryFactory get() {
		return singleton(ComponentRegistryFactory.class);
	}

	private final Map<String, IComponentRegistry> components = new ConcurrentHashMap<String, IComponentRegistry>();

	public IComponentRegistry getComponentRegistry(final String componentName) {
		return components.get(componentName);
	}

	public void registered(final IComponentRegistry componentRegistry) {
		if (componentRegistry == null) {
			return;
		}
		final String componentName = componentRegistry.getComponentName();
		if (components.containsKey(componentName)) {
			throw ComponentException.of($m("ComponentRegistryFactory.0", componentName));
		}

		try {
			final IComponentResourceProvider provider = componentRegistry
					.getComponentResourceProvider();
			if (provider != null) {
				final String[] jarPath = provider.getJarPath();
				if (jarPath != null) {
					final int length = jarPath.length;
					if (length > 0) {
						final URL[] urls = new URL[length];
						for (int i = 0; i < length; i++) {
							final String realPath = MVCUtils.getRealPath(provider.getResourceHomePath()
									+ jarPath[i]);
							urls[i] = new URL(PROTOCAL_FILE_PREFIX + realPath);
						}
						loadJarFiles(urls);
					}
				}
			}
		} catch (final IOException e) {
			throw ComponentException.of(e);
		}
		components.put(componentName, componentRegistry);
	}

	private void loadJarFiles(final URL[] urls) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (!(cl instanceof URLClassLoader)) {
			cl = ClassLoader.getSystemClassLoader();
		}
		if (cl instanceof URLClassLoader) {
			try {
				final Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
				method.setAccessible(true);
				for (final URL url : urls) {
					ClassUtils.invoke(method, cl, new Object[] { url });
				}
			} catch (final NoSuchMethodException e) {
			}
		}
	}

	public void remove(final IComponentRegistry componentRegistry) {
	}
}
