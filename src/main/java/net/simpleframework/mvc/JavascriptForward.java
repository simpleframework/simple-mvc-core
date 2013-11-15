package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class JavascriptForward extends TextForward {
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
}
