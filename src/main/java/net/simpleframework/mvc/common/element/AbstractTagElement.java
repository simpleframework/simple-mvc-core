package net.simpleframework.mvc.common.element;

import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractTagElement<T extends AbstractTagElement<T>>
		extends AbstractElement<T> {

	public AbstractTagElement() {
	}

	public AbstractTagElement(final Object text) {
		setText(text);
	}

	protected abstract String tag();

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final String tag = tag();
		sb.append("<").append(tag);
		doAttri(sb);
		sb.append(">");
		final String txt = getText();
		if (StringUtils.hasText(txt)) {
			sb.append(txt);
		}
		sb.append("</").append(tag).append(">");
		return sb.toString();
	}
}
