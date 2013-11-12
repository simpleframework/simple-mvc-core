package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.FilterChain;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class LastUrlListener implements IFilterListener, IMVCContextVar {

	@Override
	public EFilterResult doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws IOException {
		String accept;
		if (rRequest.isHttpRequest() && !ctx.isSystemUrl(rRequest)
				&& (accept = rRequest.getRequestHeader("Accept")) != null
				&& accept.contains("text/html")) {
			rRequest.setSessionAttr(SESSION_ATTRI_LASTURL, rRequest.getRequestAndQueryStringUrl());
		}
		return EFilterResult.SUCCESS;
	}

	public static String getLastUrl(final PageRequestResponse rRequest) {
		final String lastUrl = (String) rRequest.getSessionAttr(SESSION_ATTRI_LASTURL);
		rRequest.removeSessionAttr(SESSION_ATTRI_LASTURL);
		return lastUrl;
	}
}
