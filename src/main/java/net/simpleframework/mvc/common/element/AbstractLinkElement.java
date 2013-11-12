package net.simpleframework.mvc.common.element;

import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
@SuppressWarnings("unchecked")
public abstract class AbstractLinkElement<T extends AbstractTagElement<T>> extends
		AbstractTagElement<T> {
	private String href;

	private String target;

	public String getHref() {
		return StringUtils.text(href, "javascript:void(0);");
	}

	public T setHref(final String href) {
		this.href = href;
		return (T) this;
	}

	public String getTarget() {
		return target;
	}

	public T setTarget(final String target) {
		this.target = target;
		return (T) this;
	}

	@Override
	protected void doAttri(final StringBuilder sb) {
		addAttribute("target", getTarget());
		if (!isDisabled()) {
			addAttribute("href", getHref());
		}
		super.doAttri(sb);
	}

	@Override
	protected String tag() {
		return "a";
	}
}
