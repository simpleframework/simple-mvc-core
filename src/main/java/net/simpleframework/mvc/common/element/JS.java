package net.simpleframework.mvc.common.element;

import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class JS {

	public static String loc(final String url) {
		return loc(url, false);
	}

	public static String loc(final String url, final boolean open) {
		final StringBuilder sb = new StringBuilder("$Actions.loc('").append(StringUtils.blank(url))
				.append("'");
		if (open) {
			sb.append(", true");
		}
		sb.append(");");
		return sb.toString();
	}
}
