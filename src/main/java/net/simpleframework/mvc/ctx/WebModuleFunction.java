package net.simpleframework.mvc.ctx;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.ModuleFunction;
import net.simpleframework.mvc.AbstractMVCPage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class WebModuleFunction extends ModuleFunction {
	/* 功能的主操作页面的地址 */
	private String url;

	private Class<? extends AbstractMVCPage> pageClass;

	/* 显示的16px图标 */
	private String icon16, icon32, icon64;

	public WebModuleFunction() {
	}

	public WebModuleFunction(final Class<? extends AbstractMVCPage> pageClass) {
		this.pageClass = pageClass;
	}

	public String getUrl() {
		if (StringUtils.hasText(url)) {
			return url;
		}
		if (pageClass != null) {
			return AbstractMVCPage.url(pageClass);
		}
		return url;
	}

	public WebModuleFunction setUrl(final String url) {
		this.url = url;
		return this;
	}

	public Class<? extends AbstractMVCPage> getPageClass() {
		return pageClass;
	}

	public String getIcon16() {
		return icon16;
	}

	public WebModuleFunction setIcon16(final String icon16) {
		this.icon16 = icon16;
		return this;
	}

	public String getIcon32() {
		return icon32;
	}

	public WebModuleFunction setIcon32(final String icon32) {
		this.icon32 = icon32;
		return this;
	}

	public String getIcon64() {
		return icon64;
	}

	public WebModuleFunction setIcon64(final String icon64) {
		this.icon64 = icon64;
		return this;
	}

	private static final long serialVersionUID = -7070465803484944460L;
}
