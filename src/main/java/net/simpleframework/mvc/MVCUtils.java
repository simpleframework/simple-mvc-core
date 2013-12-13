package net.simpleframework.mvc;

import java.io.File;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.th.ThrowableUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.common.DeployUtils;
import net.simpleframework.mvc.component.ComponentHandlerException;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class MVCUtils implements IMVCContextVar {

	public static KVMap createVariables(final PageParameter pp) {
		final KVMap variable = new KVMap();
		variable.add("parameter", pp);
		variable.add("request", pp.request);
		variable.add("response", pp.response);
		final HttpSession session = pp.getSession();
		variable.add("session", session);
		variable.add("application", session.getServletContext());
		variable.add("pagePath", settings.getFilterPath());

		final PageDocument pageDocument = pp.getPageDocument();
		if (pageDocument != null) {
			variable.add("document", pageDocument);
			final IPageResourceProvider prp = pageDocument.getPageResourceProvider();
			variable.add("skin", prp.getSkin(pp));
			variable.add("resourcePath", prp.getResourceHomePath());

			final AbstractMVCPage pageView = pp.getPage();
			if (pageView != null) {
				variable.putAll(pageView.getVariables(pp));
			}
		}
		return variable;
	}

	public static String getRealPath(String url) {
		final String contextPath = servlet.getContextPath();
		if (StringUtils.hasText(contextPath) && url.startsWith(contextPath)) {
			url = url.substring(contextPath.length());
		}
		return servlet.getRealPath(url);
	}

	public static String getPageResourcePath() {
		return DeployUtils.getResourcePath(MVCUtils.class);
	}

	public static String getCssResourcePath(final PageParameter pp) {
		return ctx.getDefaultPageResourceProvider().getCssResourceHomePath(pp, MVCUtils.class);
	}

	public static String getLocationPath() {
		return getPageResourcePath() + "/jsp/location.jsp";
	}

	public static void setSessionSkin(final HttpSession httpSession, final String skin) {
		httpSession.setAttribute(IMVCConst.SESSION_ATTRI_SKIN, skin);
	}

	public static String doPageUrl(final PageParameter pp, final String url) {
		final PageDocument pageDocument = pp.getPageDocument();
		if (StringUtils.hasText(url) && !HttpUtils.isAbsoluteUrl(url) && !url.startsWith("/")) {
			final File documentFile = pageDocument.getDocumentFile();
			if (documentFile != null) {
				String lookupPath = documentFile.getAbsolutePath().substring(getRealPath("/").length())
						.replace(File.separatorChar, '/');
				final int pos = lookupPath.lastIndexOf("/");
				if (pos > -1) {
					lookupPath = lookupPath.substring(0, pos + 1) + url;
					return lookupPath.charAt(0) == '/' ? lookupPath : "/" + lookupPath;
				}
			} else {
				AbstractMVCPage abstractMVCPage;
				if ((abstractMVCPage = pp.getPage()) != null) {
					final String lookupPath = abstractMVCPage.getLookupPath();
					if (lookupPath != null) {
						return lookupPath.substring(0, lookupPath.lastIndexOf("/") + 1) + url;
					}
				}
			}
		}
		return url;
	}

	public static void setObjectFromRequest(final Object object, final HttpServletRequest request,
			final String prefix, final String[] properties) {
		if (object == null || properties == null) {
			return;
		}
		for (final String property : properties) {
			String value = request.getParameter(StringUtils.blank(prefix) + property);
			if ("".equals(value)) {
				value = null;
			}
			try {
				BeanUtils.setProperty(object, property, value);
			} catch (final Exception e) {
				throw ComponentHandlerException.of(e);
			}
		}
	}

	public static Throwable convertThrowable(Throwable th) {
		if (th instanceof javax.servlet.ServletException) {
			Throwable throwable = th;
			while (throwable != null
					&& javax.servlet.ServletException.class.isAssignableFrom(throwable.getClass())) {
				throwable = ((javax.servlet.ServletException) throwable).getRootCause();
				if (throwable != null) {
					th = throwable;
				}
			}
		}
		return ThrowableUtils.convertThrowable(th);
	}

	public static Map<String, Object> createException(final PageRequestResponse rRequest,
			final Throwable th) {
		final KVMap exception = new KVMap();
		exception.put("title", ctx.getThrowableMessage(th));
		final String detail = Convert.toString(th);
		exception.put("detail", detail);
		exception.put("hash", ObjectUtils.hashStr(detail));
		exception.put("more", rRequest.getLogin().isManager());
		return exception;
	}

	static Log log = LogFactory.getLogger(MVCUtils.class);
}
