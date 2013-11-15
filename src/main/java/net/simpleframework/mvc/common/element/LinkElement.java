package net.simpleframework.mvc.common.element;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.mvc.IMVCContextVar;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class LinkElement extends AbstractLinkElement<LinkElement> implements IMVCContextVar {

	public static LinkElement HOME = new LinkElement($m("LinkElement.0")).setHref(settings
			.getFilterPath());

	public static LinkElement BLANK(final Object text) {
		return new LinkElement(text).setTarget("_blank");
	}

	public static final LinkElement editLink() {
		return new LinkElement($m("Edit"));
	}

	public static final LinkElement deleteLink() {
		return new LinkElement($m("Delete"));
	}

	public LinkElement() {
	}

	public LinkElement(final Object text) {
		setText(text);
	}
}
