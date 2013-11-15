package net.simpleframework.mvc;

import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.common.xml.AbstractElementBean;
import net.simpleframework.ctx.common.xml.XmlElement;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageBean extends AbstractElementBean implements IMVCContextVar {
	private EScriptEvalScope scriptEval;

	private String scriptInit;

	private String resourceProvider;

	private String[] importPage, importJavascript, importCSS;

	private String title;

	private String favicon;

	private String responseCharacterEncoding;

	private String handleClass, handleMethod;

	private String jsLoadedCallback;

	private String role;

	private final PageDocument pageDocument;

	public PageBean(final PageDocument pageDocument, final XmlElement xmlElement) {
		super(xmlElement);
		this.pageDocument = pageDocument;
	}

	public PageBean(final PageDocument pageDocument) {
		this(pageDocument, null);
	}

	public PageDocument getPageDocument() {
		return pageDocument;
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

	public String getHandleClass() {
		return handleClass;
	}

	public PageBean setHandleClass(final String handleClass) {
		this.handleClass = handleClass;
		return this;
	}

	public String getHandleMethod() {
		return handleMethod;
	}

	public PageBean setHandleMethod(final String handleMethod) {
		this.handleMethod = handleMethod;
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

	public EScriptEvalScope getScriptEval() {
		return scriptEval == null ? EScriptEvalScope.none : scriptEval;
	}

	public PageBean setScriptEval(final EScriptEvalScope scriptEval) {
		this.scriptEval = scriptEval;
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
		return StringUtils.text(role, settings.getDefaultRole());
	}
}
