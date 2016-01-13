package net.simpleframework.mvc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.coll.ParameterMap;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.permission.LoginUser;
import net.simpleframework.ctx.permission.LoginUser.LoginWrapper;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.ctx.permission.PermissionDept;
import net.simpleframework.ctx.permission.PermissionRole;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageRequestResponse extends ObjectEx implements IMVCContextVar, IMVCConst {

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

	public long getLongParameter(final String key) {
		return Convert.toLong(getParameter(key));
	}

	public short getShortParameter(final String key) {
		return Convert.toShort(getParameter(key));
	}

	public float getFloatParameter(final String key) {
		return Convert.toFloat(getParameter(key));
	}

	public double getDoubleParameter(final String key) {
		return Convert.toDouble(getParameter(key));
	}

	public boolean getBoolParameter(final String key) {
		return Convert.toBool(getParameter(key));
	}

	public Date getDateParameter(final String key) {
		return Convert.toDate(getParameter(key));
	}

	public Date getDateParameter(final String key, final String format) {
		return Convert.toDate(getParameter(key), format);
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

	public String getRefererParam() {
		return getParameter(PARAM_REFERER);
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

	public int getServerPort() {
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

	public boolean isMultipartRequest() {
		final String type = request.getContentType();
		return type != null && type.toLowerCase().contains("multipart/");
	}

	public ServletInputStream getRequestInputStream() throws IOException {
		return request.getInputStream();
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

	public void setResponseHeader(final String name, final String value) {
		response.setHeader(name, value);
	}

	public void setResponseNoCache() {
		HttpUtils.setNoCache(response);
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
				&& settings.isGzipResponse(this) && !isHttpClientRequest();
	}

	private final static String PARAM_AJAX_REQUEST = "AJAX-REQUEST",
			PARAM_HTTP_REQUEST = "HTTP-REQUEST";

	public boolean isAjaxRequest() {
		// prototype
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
				|| getBoolParameter(PARAM_AJAX_REQUEST);
	}

	public void setHttpRequest() {
		putParameter(PARAM_HTTP_REQUEST, "true");
	}

	public boolean isHttpRequest() {
		return !(isAjaxRequest() || isHttpClientRequest()) || getBoolParameter(PARAM_HTTP_REQUEST);
	}

	public boolean isHttpClientRequest() {
		return getUserAgent().indexOf("HttpClient") > -1;
	}

	public String stripHTMLContextPath(final String html) {
		return HtmlUtils.stripContextPath(request, html);
	}

	public String wrapHTMLContextPath(final String html) {
		return HtmlUtils.wrapContextPath(request, html);
	}

	public Map<String, String> map() {
		final Map<String, String> m = new ParameterMap();
		final Enumeration<?> e = getParameterNames();
		while (e.hasMoreElements()) {
			final String key = (String) e.nextElement();
			final String val = getLocaleParameter(key);
			if (val != null) {
				m.put(key, val);
			}
		}
		return m;
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

	public String getLocalhostUrl() {
		return AbstractUrlForward.getLocalhostUrl(this);
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

	public PageRequestResponse putParameter(final String key, final Object val) {
		HttpUtils.putParameter(request, key, val);
		return this;
	}

	public String getUserAgent() {
		return request.getHeader("User-Agent");
	}

	public Float getIEVersion() {
		final String userAgent = getUserAgent();
		final int start = userAgent.indexOf("MSIE");
		if (start == -1) {
			return null;
		}
		final int end = userAgent.indexOf(";", start);
		final float ver = Convert.toFloat(userAgent.substring(start + 5, end), 0);
		return ver == 0 ? null : ver;
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

	public ID toID(final String key) {
		return ID.of(getParameter(key));
	}

	/* 权限相关的一些函数 */
	public IPagePermissionHandler getPermission() {
		return mvcContext.getPermission();
	}

	public ID getLoginId() {
		return getPermission().getLoginId(this);
	}

	public boolean isLogin() {
		return getLoginId() != null;
	}

	public PermissionRole getRole(final Object role) {
		return getPermission().getRole(role, createPermissionVars());
	}

	public Iterator<PermissionUser> users(final Object role, final ID deptId) {
		return getPermission().users(role, deptId, createPermissionVars());
	}

	public KVMap createPermissionVars() {
		return new KVMap().add(PermissionConst.VAR_USERID, getLoginId());
	}

	public PermissionUser getLogin() {
		return getRequestCache("_getLogin", new CacheV<PermissionUser>() {
			@Override
			public boolean hasVal(final PermissionUser val) {
				return val != null && val.exists();
			}

			@Override
			public PermissionUser get() {
				return getPermission().getLogin(PageRequestResponse.this);
			}
		});
	}

	public PermissionUser getUser(final Object user) {
		return getPermission().getUser(user);
	}

	public PermissionDept getLdept() {
		return getLogin().getDept();
	}

	public PermissionDept getDept(final Object dept) {
		return getPermission().getDept(dept);
	}

	public ID getLDomainId() {
		return getLdept().getDomainId();
	}

	public boolean isLmember(final Object role, final Map<String, Object> variables) {
		return getLogin().isMember(role, variables);
	}

	public boolean isLmember(final Object role) {
		return getLogin().isMember(role);
	}

	public boolean isLmanager() {
		return getLogin().isManager();
	}

	public void setLoginUser(final Object user) {
		LoginUser.set(new LoginWrapper(getUser(user)).setIp(getRemoteAddr()));
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

	public void clearPhotoCache(final Object user) throws IOException {
		getPermission().clearPhotoCache(this, user);
	}

	@SuppressWarnings("unchecked")
	public <T> T getRequestCache(final String key, final CacheV<T> i) {
		T val = (T) getRequestAttr(key);
		if (val == null && i.hasVal(val = i.get())) {
			setRequestAttr(key, val);
		}
		return val;
	}

	@SuppressWarnings("unchecked")
	public <T> T getSessionCache(final String key, final CacheV<T> i) {
		T val = (T) getSessionAttr(key);
		if (val == null && i.hasVal(val = i.get())) {
			setSessionAttr(key, val);
		}
		return val;
	}

	public static PageRequestResponse get(final HttpServletRequest request,
			final HttpServletResponse response) {
		return new PageRequestResponse(request, response);
	}

	public static class UserAgent {
		public String brower;
		public float ver;

		public UserAgent(final String userAgent) {
			if (userAgent.indexOf("MSIE") != -1) {
				brower = "IE";
				final int start = userAgent.indexOf("MSIE");
				final int end = userAgent.indexOf(";", start);
				ver = Convert.toFloat(userAgent.substring(start + 5, end));
			} else if (userAgent.indexOf("Firefox") != -1) {
				brower = "Firefox";
				final int start = userAgent.indexOf("Firefox/");
				ver = Convert.toFloat(userAgent.substring(start + "Firefox".length() + 1,
						userAgent.length()));
			} else if (userAgent.indexOf("Chrome") != -1) {
				brower = "Chrome";
				final int start = userAgent.indexOf("Chrome/");
				int end = userAgent.indexOf(".", start);
				end = userAgent.indexOf(".", end + 1);
				ver = Convert.toFloat(userAgent.substring(start + "Chrome".length() + 1, end));
			} else if (userAgent.indexOf("Safari") != -1) {
				// Version/5.1.2
				brower = "Safari";
				final int start = userAgent.indexOf("Version/");
				final int end = userAgent.indexOf(" ", start);
				ver = Convert.toFloat(userAgent.substring(start + "Version".length() + 1, end));
			}
		}
	}

	protected final Log log = LogFactory.getLogger(getClass());
}
