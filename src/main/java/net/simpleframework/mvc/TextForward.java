package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class TextForward extends AbstractForward {

	protected final StringBuilder builder = new StringBuilder();

	public TextForward() {
	}

	public TextForward(final String responseText) {
		append(responseText);
	}

	public TextForward append(final Object text) {
		if (text != null) {
			builder.append(text);
		}
		return this;
	}

	public TextForward insert(final int offset, final Object text) {
		if (text != null) {
			builder.insert(offset, text);
		}
		return this;
	}

	public void clear() {
		builder.setLength(0);
	}

	@Override
	public String getResponseText(final PageRequestResponse rRequest) {
		return toString();
	}

	@Override
	public String toString() {
		return builder.toString();
	}
}
