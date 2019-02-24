package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.http.Cookie;

import net.simpleframework.common.StringUtils;
import net.simpleframework.lib.org.jsoup.Connection;
import net.simpleframework.lib.org.jsoup.Jsoup;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UrlForward extends AbstractUrlForward {

	public static UrlForward REDIRECT_HOME = UrlForward.redirect("/");

	public static UrlForward redirect(final String url) {
		return new UrlForward(url).setRedirect(true);
	}

	private boolean redirect;

	public UrlForward(final String url, final String includeRequestData) {
		super(url, includeRequestData);
	}

	public UrlForward(final String url) {
		super(url);
	}

	public boolean isRedirect() {
		return redirect;
	}

	public UrlForward setRedirect(final boolean redirect) {
		this.redirect = redirect;
		return this;
	}

	@Override
	public String getResponseText(final PageRequestResponse rRequest) {
		final String url = getRequestUrl(rRequest);
		try {
			final Connection conn = Jsoup.connect(url)
					.userAgent("HttpClient-[" + rRequest.getRequestHeader("User-Agent") + "]")
					.maxBodySize(0).timeout(10 * 60 * 1000);
			conn.header("x-forwarded-for", rRequest.getRemoteAddr());
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
		sb.append("<div class='").append(MVCConst.HTML_ENCODE_CLASS).append("'>")
				.append(StringUtils.encodeHex(text.getBytes())).append("</div>");
		return sb.toString();
	}

	public static String getCookieRedirectUrl(final PageRequestResponse rRequest) {
		final String redirect_url = rRequest.getCookie("redirect_url");
		rRequest.addCookie("redirect_url", null);
		return redirect_url;
	}
}
