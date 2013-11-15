package net.simpleframework.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.UserAgentParser;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageRequestResponse implements IMVCContextVar, IMVCConst {

	public HttpServletRequest request;

	public HttpServletResponse response;

	public PageRequestResponse(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	private HttpServletRequest getPageRequest(HttpServletRequest request) {
		while (request instanceof HttpServletRequestWrapper) {
			if (!(request instanceof PageRequest)) {
				request = (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest();
			} else {
				return request;
			}
		}
		return request;
	}

	// Request Wrapper

	public String[] getParameterValues(final String key, final boolean encode) {
		final String[] vals = request.getParameterValues(key);
		if (encode && vals != null) {
			for (int i = 0; i < vals.length; i++) {
				try {
					vals[i] = URLEncoder.encode(vals[i], settings.getCharset());
				} catch (final UnsupportedEncodingException e) {
				}
			}
		}
		return vals;
	}

	public String[] getParameterValues(final String key) {
		return getParameterValues(key, false);
	}

	public String getParameter(final String key, final boolean encode) {
		final String[] vals = getParameterValues(key, encode);
		return vals != null && vals.length > 0 ? vals[0] : null;
	}

	public String getParameter(final String key) {
		return getParameter(key, false);
	}

	public String getLocaleParameter(final String key) {
		return HttpUtils.toLocaleString(getParameter(key), settings.getCharset());
	}

	public int getIntParameter(final String key) {
		return Convert.toInt(getParameter(key));
	}

	public short getShortParameter(final String key) {
		return Convert.toShort(getParameter(key));
	}

	public double getDoubleParameter(final String key) {
		return Convert.toDouble(getParameter(key));
	}

	public boolean getBoolParameter(final String key) {
		return Convert.toBool(getParameter(key));
	}

	public <T extends Enum<T>> T getEnumParameter(final Class<T> enumClass, final String key) {
		final String val = getParameter(key);
		if (StringUtils.hasText(val)) {
			return Convert.toEnum(enumClass, val);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> getParameterNames() {
		return request.getParameterNames();
	}

	public Object getRequestAttr(final String key) {
		return getPageRequest(request).getAttribute(key);
	}

	public void setRequestAttr(final String key, final Object value) {
		getPageRequest(request).setAttribute(key, value);
	}

	public void removeRequestAttr(final String key) {
		getPageRequest(request).removeAttribute(key);
	}

	public Enumeration<?> getRequestAttrNames() {
		return request.getAttributeNames();
	}

	public String getRequestURI() {
		return HttpUtils.getRequestURI(request);
	}

	public String getQueryString() {
		return HttpUtils.getQueryString(request, true);
	}

	public Enumeration<?> getRequestHeaderNames() {
		return request.getHeaderNames();
	}

	public String getRequestHeader(final String key) {
		return request.getHeader(key);
	}

	public Enumeration<?> getRequestHeaders(final String name) {
		return request.getHeaders(name);
	}

	public int getServletPort() {
		return request.getServerPort();
	}

	public String getRequestScheme() {
		return request.getScheme();
	}

	public String getRemoteAddr() {
		return HttpUtils.getRemoteAddr(request);
	}

	public String getRequestCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	public void setRequestCharacterEncoding(final String encoding)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding(encoding);
	}

	public Cookie[] getRequestCookies() {
		return request.getCookies();
	}

	// Response Wrapper

	public PrintWriter getResponseWriter() throws IOException {
		return response.getWriter();
	}

	public String getResponseContentType() {
		return response.getContentType();
	}

	public void setResponseContentType(final String type) {
		response.setContentType(type);
	}

	public void setResponseCharacterEncoding(final String charset) {
		response.setCharacterEncoding(charset);
	}

	// Session Wrapper

	public HttpSession getSession() {
		return request.getSession();
	}

	public String getSessionId() {
		return getSession().getId();
	}

	public Object getSessionAttr(final String key) {
		return getSession().getAttribute(key);
	}

	public void setSessionAttr(final String key, final Object value) {
		getSession().setAttribute(key, value);
	}

	public void removeSessionAttr(final String key) {
		getSession().removeAttribute(key);
	}

	// ServletContext Wrapper

	public ServletContext getServletContext() {
		return getSession().getServletContext();
	}

	public String getContextPath() {
		return getServletContext().getContextPath();
	}

	/*-----------------------utils--------------------*/
	public boolean isGzipResponse() {
		final String browserEncodings = getRequestHeader("accept-encoding");
		return ((browserEncodings != null) && (browserEncodings.indexOf("gzip") != -1))
				&& ctx.getMVCSettings().isGzipResponse(this) && !isHttpClientRequest();
	}

	public boolean isHttpClientRequest() {
		return getUserAgent().indexOf("HttpClient") > -1;
	}

	public boolean isAjaxRequest() {
		// prototype
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
				|| getBoolParameter(PARAM_AJAX_REQUEST_MARK);
	}

	public boolean isHttpRequest() {
		return !isAjaxRequest() && !isHttpClientRequest();
	}

	public String stripHTMLContextPath(final String html) {
		return HtmlUtils.stripContextPath(request, html);
	}

	public String wrapHTMLContextPath(final String html) {
		return HtmlUtils.wrapContextPath(request, html);
	}

	/*-----------------------utils cookie--------------------*/
	public String getCookie(final String key) {
		return HttpUtils.getCookie(request, key);
	}

	public void addCookie(final String key, final Object value, final boolean secure,
			final String path, final int age) {
		HttpUtils.addCookie(response, key, value, secure, path, age);
	}

	public void addCookie(final String key, final Object value, final int age) {
		HttpUtils.addCookie(response, key, value, age);
	}

	public void addCookie(final String key, final Object value) {
		HttpUtils.addCookie(response, key, value);
	}

	/*-----------------------utils include--------------------*/
	public String includeUrl(final String url) {
		return UrlForward.includeResponseText(UrlForward.getResponseText(this, url));
	}

	public String includeUrl(final Class<? extends AbstractMVCPage> pageClass) {
		return includeUrl(AbstractMVCPage.url(pageClass));
	}

	public String includeUrl(final Class<? extends AbstractMVCPage> pageClass,
			final String queryString) {
		return includeUrl(AbstractMVCPage.url(pageClass, queryString));
	}

	public String includeUrl(final String url, final String includeRequestData) {
		return UrlForward.includeResponseText(new UrlForward(url, includeRequestData)
				.getResponseText(this));
	}

	/*-----------------------utils url--------------------*/
	public Map<String, Object> toQueryParams(final String... keys) {
		final KVMap kv = new KVMap();
		if (keys != null) {
			for (final String key : keys) {
				final String val = getParameter(key);
				if (val != null) {
					kv.put(key, val);
				}
			}
		}
		return kv;
	}

	public void putParameter(final String key, final Object val) {
		HttpUtils.putParameter(request, key, val);
	}

	public String getUserAgent() {
		return request.getHeader("User-Agent");
	}

	public UserAgentParser getUserAgentParser() {
		return UserAgentParser.get(request);
	}

	public boolean loc(final String url) throws IOException {
		return HttpUtils.loc(request, response, url);
	}

	public String stripJSessionId(final String url) {
		return HttpUtils.stripJSessionId(url);
	}

	public String stripContextPath(final String url) {
		return HttpUtils.stripContextPath(request, url);
	}

	public String wrapContextPath(final String url) {
		return HttpUtils.wrapContextPath(request, url);
	}

	public String getRequestAndQueryStringUrl() {
		return HttpUtils.getRequestAndQueryStringUrl(request);
	}

	/*-----------------------utils stream--------------------*/
	public OutputStream getBinaryOutputStream(final String filename) throws IOException {
		return getBinaryOutputStream(filename, 0);
	}

	public OutputStream getBinaryOutputStream(final String filename, final long filesize)
			throws IOException {
		return HttpUtils.getBinaryOutputStream(request, response, filename, filesize);
	}

	/* 权限相关的一些函数 */
	public IPagePermissionHandler getPermission() {
		return MVCContext.permission();
	}

	public ID getLoginId() {
		return getPermission().getLoginId(this);
	}

	public boolean isLogin() {
		return getLoginId() != null;
	}

	public PermissionUser getLogin() {
		return getPermission().getLogin(this);
	}

	public PermissionUser getUser(final Object user) {
		return getPermission().getUser(user);
	}

	public String getPhotoUrl() {
		return getPhotoUrl(getLogin());
	}

	public String getPhotoUrl(final Object user) {
		return getPermission().getPhotoUrl(this, user);
	}

	public String getPhotoUrl(final Object user, final int width, final int height) {
		return getPermission().getPhotoUrl(this, user, width, height);
	}

	public static PageRequestResponse get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return new PageRequestResponse(request, response);
	}
}
