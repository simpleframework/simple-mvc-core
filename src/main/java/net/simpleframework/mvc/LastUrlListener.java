package net.simpleframework.mvc;

import java.io.IOException;

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

	public static void set(final PageRequestResponse rRequest, final String url) {
		getInstance().setLastUrl(rRequest, url);
	}

	public LastUrlListener() {
		instance = this;
	}

	@Override
	public EFilterResult doFilter(final PageRequestResponse rRequest,
			final PageDocument pageDocument) throws IOException {
		String accept;
		if (rRequest.isHttpRequest() && (accept = rRequest.getRequestHeader("Accept")) != null
				&& accept.contains("text/html")) {
			final String last_url = rRequest.getParameter("last_url");
			if (StringUtils.hasText(last_url)) {
				setLastUrl(rRequest, last_url);
			} else {
				if (!MVCContext.get().isSystemUrl(rRequest)) {
					setLastUrl(rRequest, rRequest.getRequestAndQueryStringUrl());

					final String redirect_url = rRequest.getParameter("redirect_url");
					if (StringUtils.hasText(redirect_url)) {
						rRequest.addCookie("redirect_url", redirect_url);
					}
				}
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
