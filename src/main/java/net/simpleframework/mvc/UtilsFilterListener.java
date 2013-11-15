package net.simpleframework.mvc;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.UserAgentParser;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UtilsFilterListener implements IFilterListener, IMVCContextVar {

	@Override
	public EFilterResult doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws IOException {
		final HttpServletRequest httpRequest = rRequest.request;

		final String ieWarnUrl = settings.getIEWarnPath(rRequest);
		if (StringUtils.hasText(ieWarnUrl)) {
			final UserAgentParser parser = rRequest.getUserAgentParser();
			if (parser.isIE() && parser.getBrowserFloatVersion() < 8.0
					&& !Convert.toBool(rRequest.getCookie("ie6_browser"))
					&& !httpRequest.getRequestURI().endsWith(ieWarnUrl)) {
				HttpUtils.loc(httpRequest, rRequest.response, ieWarnUrl);
				return EFilterResult.BREAK;
			}
		}

		/* encoding */
		final String encoding = settings.getCharset();
		if (encoding != null) {
			rRequest.setRequestCharacterEncoding(encoding);
		}
		return EFilterResult.SUCCESS;
	}
}