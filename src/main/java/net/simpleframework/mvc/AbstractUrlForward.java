package net.simpleframework.mvc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.th.RuntimeExceptionEx;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractUrlForward extends ObjectEx implements IForward, IMVCContextVar,
		IMVCConst {
	private String url;

	private String includeRequestData;

	public AbstractUrlForward(final String url, final String includeRequestData) {
		this.url = url;
		this.includeRequestData = includeRequestData;
	}

	public AbstractUrlForward(final String url) {
		this(url, null);
	}

	protected String getRequestUrl(final PageRequestResponse rRequest) {
		final StringBuilder sb = new StringBuilder();

		final Map<String, Object> qMap = HttpUtils.toQueryParams(putRequestData(rRequest,
				getIncludeRequestData()));
		final String url = getUrl();
		final int qp = url.indexOf("?");
		if (qp > -1) {
			sb.append(url.substring(0, qp));
			qMap.putAll(HttpUtils.toQueryParams(url.substring(qp + 1)));
		} else {
			sb.append(url);
		}

		/* 加入PageDocument */
		PageDocument doc;
		if (rRequest instanceof PageParameter
				&& (doc = ((PageParameter) rRequest).getPageDocument()) != null) {
			qMap.put(PARAM_DOCUMENT, doc.hashId());
		}
		/* 加入引用地址 */
		if (!StringUtils.hasText(rRequest.getParameter(PARAM_REFERER))) {
			qMap.put(PARAM_REFERER, rRequest.getRequestAndQueryStringUrl());
		}

		/* 加入jsessionid */
		String jsessionid = rRequest.getParameter(JSESSIONID);
		if (!StringUtils.hasText(jsessionid)) {
			jsessionid = rRequest.getSessionId();
		}
		sb.append(";").append(JSESSIONID).append("=").append(jsessionid);
		sb.append("?").append(JSESSIONID).append("=").append(jsessionid).append("&")
				.append(HttpUtils.toQueryString(qMap));
		return getLocalhostUrl(rRequest) + rRequest.wrapContextPath(sb.toString());
	}

	public String getUrl() {
		return url;
	}

	public AbstractUrlForward setUrl(final String url) {
		this.url = url;
		return this;
	}

	public String getIncludeRequestData() {
		return includeRequestData;
	}

	public AbstractUrlForward setIncludeRequestData(final String includeRequestData) {
		this.includeRequestData = includeRequestData;
		return this;
	}

	protected RuntimeException convertRuntimeException(final Exception ex, final String url) {
		log.warn(ex);
		return RuntimeExceptionEx._of(MVCException.class, "url: " + url, ex);
	}

	public static String putRequestData(final PageRequestResponse rRequest,
			final String includeRequestData) {
		return putRequestData(rRequest, includeRequestData, false);
	}

	static Object lock = new Object();

	static long COUNTER = 0;

	static final String KEEP_REQUESTDATA_CACHE = "keep_requestdata_cache";

	public static String putRequestData(final PageRequestResponse rRequest,
			final String includeRequestData, final boolean keepCache) {
		String requestId;
		synchronized (lock) {
			requestId = String.valueOf(COUNTER++);
		}

		SessionCache.lput(requestId, new RequestData(rRequest, includeRequestData));
		String p = REQUEST_ID + "=" + requestId;
		if (keepCache) {
			p += "&" + KEEP_REQUESTDATA_CACHE + "=true";
		}
		return p;
	}

	static RequestData getRequestDataByRequest(final HttpServletRequest httpRequest) {
		final String requestId = httpRequest.getParameter(REQUEST_ID);
		if (Convert.toBool(httpRequest.getParameter(KEEP_REQUESTDATA_CACHE))) {
			return (RequestData) SessionCache.lget(requestId);
		} else {
			return (RequestData) SessionCache.lremove(requestId);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static class RequestData {
		final Map parameters = new HashMap();

		final Map attributes = new HashMap();

		final Map headers = new HashMap();

		public RequestData(final PageRequestResponse rRequest, String includeRequestData) {
			includeRequestData = StringUtils.text(includeRequestData, "p").toLowerCase();

			// parameters
			if (includeRequestData.contains("p")) {
				parameters.putAll(rRequest.request.getParameterMap());
				for (final String k : settings.getSystemParamKeys()) {
					parameters.remove(k);
				}
			}

			// attributes
			final Enumeration<?> attributeNames = rRequest.getRequestAttrNames();
			while (attributeNames.hasMoreElements()) {
				final String name = (String) attributeNames.nextElement();
				if (includeRequestData.contains("a")
						|| name.startsWith(ComponentUtils.REQUEST_HANDLE_KEY)) {
					final Object value = rRequest.getRequestAttr(name);
					if (value != null) {
						attributes.put(name, value);
					}
				}
			}

			// headers
			if (includeRequestData.contains("h")) {
				final Enumeration<?> headerNames = rRequest.getRequestHeaderNames();
				while (headerNames.hasMoreElements()) {
					final String name = (String) headerNames.nextElement();
					final Enumeration e = rRequest.getRequestHeaders(name);
					if (e != null) {
						headers.put(name, e);
					}
				}
			}
		}
	}

	public static String getLocalhostUrl(final PageRequestResponse rRequest) {
		final StringBuilder sb = new StringBuilder();
		sb.append(rRequest.getRequestScheme()).append("://localhost:")
				.append(settings.getServletPort(rRequest));
		return sb.toString();
	}

	public static AbstractUrlForward componentUrl(
			final Class<? extends AbstractComponentBean> beanClass, final String url) {
		return new UrlForward(ComponentUtils.getResourceHomePath(beanClass) + url);
	}
}
