package net.simpleframework.mvc;

import net.simpleframework.common.web.JavascriptUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class JavascriptForward extends TextForward {
	public static final JavascriptForward RELOC = new JavascriptForward("$Actions.reloc();");

	public static final JavascriptForward alert(final String msg) {
		return new JavascriptForward("alert(\"").append(JavascriptUtils.escape(msg)).append("\");");
	}

	public JavascriptForward(final String javascript) {
		super(javascript);
	}

	public JavascriptForward() {
		this(null);
	}

	@Override
	public JavascriptForward append(final Object javascript) {
		return (JavascriptForward) super.append(javascript);
	}

	@Override
	public boolean isHtmlParser() {
		return false;
	}
}
