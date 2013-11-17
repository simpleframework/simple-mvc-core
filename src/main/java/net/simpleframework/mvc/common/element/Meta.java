package net.simpleframework.mvc.common.element;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.NamedObject;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class Meta extends NamedObject<Meta> {
	private String httpEquiv;

	private String content;

	public Meta(final String httpEquiv, final String content) {
		this.httpEquiv = httpEquiv;
		this.content = content;
	}

	public Meta(final String content) {
		this(null, content);
	}

	public String getHttpEquiv() {
		return httpEquiv;
	}

	public Meta setHttpEquiv(final String httpEquiv) {
		this.httpEquiv = httpEquiv;
		return this;
	}

	public String getContent() {
		return content;
	}

	public Meta setContent(final String content) {
		this.content = content;
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<meta");
		final String name = getName();
		if (StringUtils.hasText(name)) {
			sb.append(" name=\"").append(name).append("\"");
		} else {
			final String httpEquiv = getHttpEquiv();
			if (StringUtils.hasText(httpEquiv)) {
				sb.append(" http-equiv=\"").append(httpEquiv).append("\"");
			}
		}
		final String content = getContent();
		if (StringUtils.hasText(content)) {
			sb.append(" content=\"").append(content).append("\"");
		}
		sb.append(" />");
		return sb.toString();
	}

	public static Meta contentType(final String val) {
		return new Meta("Content-Type", val);
	}

	public static Meta DEFAULT_COMPATIBLE = new Meta("X-UA-Compatible", "IE=edge,chrome=1");

	public static Meta RENDERER_WEBKIT = new Meta("webkit").setName("renderer");

	public static Meta GOOGLE_NOTRANSLATE = new Meta("google", "notranslate");

	public static Meta CLEARTYPE = new Meta("cleartype", "on");

	public static Meta DEFAULT_VIEWPORT = new Meta("width=device-width, initial-scale=1")
			.setName("viewport");

	public static Meta ROBOTS_NONE = new Meta("none").setName("robots");

	public static Meta ROBOTS_ALL = new Meta("all").setName("robots");
}
