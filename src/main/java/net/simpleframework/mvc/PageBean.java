package net.simpleframework.mvc;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.common.xml.AbstractElementBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageBean extends AbstractElementBean implements IMVCSettingsAware {
	private static final long serialVersionUID = -6963023908718020160L;

	/* 是否禁用文档解析 */
	private boolean disabled;

	/* 页面的执行类及方法 */
	private String handlerClass, handlerMethod;

	private EEvalScope evalScope;

	private String scriptInit;

	/* 页面资源的提供类 */
	private String resourceProvider;

	private String[] importPage, importJavascript, importCSS;

	private String title;

	private String favicon;

	private String responseCharacterEncoding;

	private String jsLoadedCallback;

	/* 访问该页面的角色 */
	private String role;

	private final PageDocument pageDocument;

	public PageBean(final PageDocument pageDocument) {
		this.pageDocument = pageDocument;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public PageBean setDisabled(final boolean disabled) {
		this.disabled = disabled;
		return this;
	}

	public String getHandlerClass() {
		return handlerClass;
	}

	public PageBean setHandlerClass(final String handlerClass) {
		this.handlerClass = handlerClass;
		return this;
	}

	public String getHandlerMethod() {
		return handlerMethod;
	}

	public PageBean setHandlerMethod(final String handlerMethod) {
		this.handlerMethod = handlerMethod;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public PageBean setTitle(final String title) {
		this.title = title;
		return this;
	}

	public String getFavicon() {
		if (!StringUtils.hasText(favicon)) {
			favicon = MVCUtils.getPageResourcePath() + "/images/favicon.png";
		}
		return favicon;
	}

	public PageBean setFavicon(final String favicon) {
		this.favicon = favicon;
		return this;
	}

	public String getResourceProvider() {
		return resourceProvider;
	}

	public PageBean setResourceProvider(final String resourceProvider) {
		this.resourceProvider = resourceProvider;
		return this;
	}

	public String getJsLoadedCallback() {
		return jsLoadedCallback;
	}

	public PageBean setJsLoadedCallback(final String jsLoadedCallback) {
		this.jsLoadedCallback = jsLoadedCallback;
		return this;
	}

	public String[] getImportPage() {
		return importPage;
	}

	public PageBean setImportPage(final String[] importPage) {
		this.importPage = importPage;
		return this;
	}

	public EEvalScope getEvalScope() {
		return evalScope == null ? EEvalScope.none : evalScope;
	}

	public PageBean setEvalScope(final EEvalScope evalScope) {
		this.evalScope = evalScope;
		return this;
	}

	public String getScriptInit() {
		return scriptInit;
	}

	public PageBean setScriptInit(final String scriptInit) {
		this.scriptInit = scriptInit;
		return this;
	}

	public String[] getImportJavascript() {
		return importJavascript;
	}

	public PageBean setImportJavascript(final String[] importJavascript) {
		this.importJavascript = importJavascript;
		return this;
	}

	public String[] getImportCSS() {
		return importCSS;
	}

	public PageBean setImportCSS(final String[] importCSS) {
		this.importCSS = importCSS;
		return this;
	}

	public String getResponseCharacterEncoding() {
		return responseCharacterEncoding;
	}

	public PageBean setResponseCharacterEncoding(final String responseCharacterEncoding) {
		this.responseCharacterEncoding = responseCharacterEncoding;
		return this;
	}

	public String getRole() {
		return role;
	}

	public PageBean setRole(final String role) {
		this.role = role;
		return this;
	}

	protected String getDefaultRole(final String role) {
		return StringUtils.hasText(role) ? role : mvcSettings.getDefaultRole();
	}

	public PageDocument getPageDocument() {
		return pageDocument;
	}
}
