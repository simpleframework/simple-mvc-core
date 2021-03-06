package net.simpleframework.mvc;

import java.io.IOException;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class UtilsFilterListener implements IFilterListener {

	@Override
	public EFilterResult doFilter(final PageRequestResponse rRequest,
			final PageDocument pageDocument) throws IOException {
		final String ieWarnUrl;
		if (rRequest.isHttpRequest() && !rRequest.getBoolParameter("iewarn_disabled")
				&& StringUtils.hasText(ieWarnUrl = mvcSettings.getIEWarnPath(rRequest))
				&& !"false".equals(ieWarnUrl)) {
			final Boolean ie = rRequest.isIE8("<");
			if ((ie != null && ie) && !Convert.toBool(rRequest.getCookie("ie6_browser"))
					&& !rRequest.getRequestURI().endsWith(ieWarnUrl)) {
				rRequest.loc(ieWarnUrl);
				return EFilterResult.BREAK;
			}
		}

		/* encoding */
		final String encoding = mvcSettings.getCharset();
		if (encoding != null) {
			rRequest.setRequestCharacterEncoding(encoding);
		}
		return EFilterResult.SUCCESS;
	}
}