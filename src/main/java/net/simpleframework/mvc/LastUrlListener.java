package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.FilterChain;

import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class LastUrlListener implements IFilterListener {

	private static LastUrlListener instance;

	public static LastUrlListener getInstance() {
		return instance;
	}

	public LastUrlListener() {
		instance = this;
	}

	@Override
	public EFilterResult doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws IOException {
		String accept;
		if (rRequest.isHttpRequest() && !MVCContext.get().isSystemUrl(rRequest)
				&& (accept = rRequest.getRequestHeader("Accept")) != null
				&& accept.contains("text/html")) {
			setLastUrl(rRequest, rRequest.getRequestAndQueryStringUrl());

			final String redirect_url = rRequest.getParameter("redirect_url");
			if (StringUtils.hasText(redirect_url)) {
				rRequest.addCookie("redirect_url", redirect_url);
			}
		}
		return EFilterResult.SUCCESS;
	}

	protected void setLastUrl(final PageRequestResponse rRequest, final String url) {
		rRequest.setSessionAttr(MVCConst.SESSION_ATTRI_LASTURL, url);
	}

	public String getLastUrl(final PageRequestResponse rRequest) {
		final String lastUrl = (String) rRequest.getSessionAttr(MVCConst.SESSION_ATTRI_LASTURL);
		// rRequest.removeSessionAttr(MVCConst.SESSION_ATTRI_LASTURL);
		return lastUrl;
	}
}
