package net.simpleframework.mvc.common;

import static net.simpleframework.common.I18n.$m;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.ClassUtils.IScanResourcesCallback;
import net.simpleframework.common.Convert;
import net.simpleframework.common.FileUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.mvc.IMVCContextVar;
import net.simpleframework.mvc.MVCUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DeployUtils implements IMVCContextVar {

	private final static String RESOURCE_NAME = "$resource";

	public static IScanResourcesCallback newDeployResourcesCallback() {
		ObjectEx.oprintln($m("DeployUtils.0"));
		return new IScanResourcesCallback() {
			private final Map<String, Properties> rProperties = new HashMap<String, Properties>();

			private String getDeployName(final String filename) {
				final StringBuilder sb = new StringBuilder();
				String packageName = filename.substring(0, filename.lastIndexOf('/')).replace('/', '.');
				if (!settings.isDebug()) {
					packageName = ObjectUtils.hashStr(packageName);
				}
				sb.append("/").append(RESOURCE_NAME).append("/").append(packageName);
				return sb.toString();
			}

			@Override
			public void doResources(String filepath, final boolean isDirectory) throws IOException {
				if (filepath.endsWith("/")) {
					filepath = filepath.substring(0, filepath.length() - 1);
				}
				if (isDirectory && filepath.endsWith(RESOURCE_NAME)) {
					// 开始部署
					final Properties properties = new Properties();
					rProperties.put(filepath, properties);
					properties.setProperty("$$filename", MVCUtils.getRealPath(getDeployName(filepath)));
					final InputStream inputStream = ClassUtils.getResourceAsStream(filepath
							+ "/settings");
					if (inputStream != null) {
						properties.load(inputStream);
					}
				} else {
					Properties properties;
					// filepath 可能是rProperties中的某一个resourceMark
					for (final String resourceMark : rProperties.keySet()) {
						if (filepath.startsWith(resourceMark)
								&& (properties = rProperties.get(resourceMark)) != null) {
							final InputStream inputStream = ClassUtils.getResourceAsStream(filepath);
							String filename;
							if (inputStream != null
									&& (filename = properties.getProperty("$$filename")) != null) {
								final File to = new File(filename
										+ filepath.substring(resourceMark.length()).replace('/',
												File.separatorChar));
								if (isDirectory) {
									FileUtils.createDirectoryRecursively(to);
								} else {
									final boolean compress = settings.isResourceCompress();
									JavascriptUtils.copyFile(inputStream, to,
											Convert.toBool(properties.get("jsCompress"), compress),
											Convert.toBool(properties.get("cssCompress"), compress));
								}
							}
						}
					}
				}
			}
		};
	}

	public static String getResourcePath(final Class<?> resourceClass) {
		String resourceUrl = resourceUrlCache.get(resourceClass);
		if (resourceUrl == null) {
			String packageName = resourceClass.getPackage().getName();
			if (!settings.isDebug()) {
				packageName = ObjectUtils.hashStr(packageName);
			}
			final StringBuilder sb = new StringBuilder();
			sb.append(servlet.getContextPath());
			sb.append("/").append(RESOURCE_NAME).append("/").append(packageName);
			resourceUrlCache.put(resourceClass, resourceUrl = sb.toString());
		}
		return resourceUrl;
	}

	private static Map<Class<?>, String> resourceUrlCache = new ConcurrentHashMap<Class<?>, String>();
}
