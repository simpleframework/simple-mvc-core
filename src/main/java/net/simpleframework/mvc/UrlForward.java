package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.http.Cookie;

import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.lib.org.jsoup.Connection;
import net.simpleframework.lib.org.jsoup.Jsoup;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UrlForward extends AbstractUrlForward {

	public UrlForward(final String url, final String includeRequestData) {
		super(url, includeRequestData);
	}

	public UrlForward(final String url) {
		super(url);
	}

	@Override
	public String getResponseText(final PageRequestResponse rRequest) {
		final String url = getRequestUrl(rRequest);
		try {
			final Connection conn = Jsoup.connect(url)
					.userAgent("HttpClient-[" + rRequest.getUserAgent() + "]").maxBodySize(0).timeout(0);
			final Cookie[] cookies = rRequest.getRequestCookies();
			if (cookies != null) {
				for (final Cookie cookie : cookies) {
					// jsessionid
					conn.cookie(cookie.getName(), cookie.getValue());
				}
			}
			return conn.execute().body();
		} catch (final IOException e) {
			throw convertRuntimeException(e, url);
		}
	}

	public static String getResponseText(final PageRequestResponse rRequest, final String url) {
		return new UrlForward(url).getResponseText(rRequest);
	}

	public static String includeResponseText(final String text) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='").append(HTML_BASE64_CLASS).append("'>")
				.append(AlgorithmUtils.base64Encode(text.getBytes())).append("</div>");
		return sb.toString();
	}
}
