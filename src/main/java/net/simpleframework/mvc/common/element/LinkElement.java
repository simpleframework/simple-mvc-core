package net.simpleframework.mvc.common.element;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.ctx.ModuleFunction;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IMVCSettingsAware;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class LinkElement extends AbstractLinkElement<LinkElement> implements IMVCSettingsAware {

	public static LinkElement style2(final Object text) {
		return new LinkElement(text).setClassName("simple_btn2");
	}

	public static LinkElement style2(final Object text,
			final Class<? extends AbstractMVCPage> pageClass) {
		return new LinkElement(text, pageClass).setClassName("simple_btn2");
	}

	public static LinkElement HOME(final PageRequestResponse rRequest) {
		return new LinkElement($m("LinkElement.0")).setHref(mvcSettings.getHomePath(rRequest));
	}

	public static LinkElement BLANK(final Object text) {
		return new LinkElement(text).blank();
	}

	public static LinkElement BLANK(final Object text, final String href) {
		return BLANK(text).setHref(href);
	}

	public static final LinkElement editLink() {
		return new LinkElement($m("Edit"));
	}

	public static final LinkElement deleteLink() {
		return new LinkElement($m("Delete"));
	}

	public static LinkElement of(final ModuleFunction mf) {
		final LinkElement le = new LinkElement(mf.getText());
		if (mf instanceof WebModuleFunction) {
			le.setHref(((WebModuleFunction) mf).getUrl());
		}
		return le;
	}

	public LinkElement() {
	}

	public LinkElement(final Object text, final Class<? extends AbstractMVCPage> pageClass) {
		this(text);
		setHref(AbstractMVCPage.url(pageClass));
	}

	public LinkElement(final Object text) {
		setText(text);
	}

	public LinkElement(final Object text, final String className) {
		setText(text);
		setClassName(className);
	}

	private static final long serialVersionUID = 7837268001108185939L;
}
