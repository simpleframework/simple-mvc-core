package net.simpleframework.mvc;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.Convert;
import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.common.web.html.HtmlConst;
import net.simpleframework.mvc.IFilterListener.EFilterResult;
import net.simpleframework.mvc.common.element.JS;
import net.simpleframework.mvc.parser.PageParser;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCFilter extends ObjectEx implements Filter {

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		final String handler = filterConfig.getInitParameter("mvc-context");
		final IMVCContext ctx = StringUtils.hasText(handler) ? (IMVCContext) singleton(handler)
				: createMVCContext();
		try {
			ctx.setServletContext(filterConfig.getServletContext());
			ctx.setScanPackageNames(StringUtils.split(filterConfig.getInitParameter("scan-packages")));
			ctx.onInit();
		} catch (final Exception e) {
			getLog().error(e);
		}
	}

	protected IMVCContext createMVCContext() throws ServletException {
		return new MVCContext();
	}

	protected PageRequestResponse createPageRequestResponse(final HttpServletRequest request,
			final HttpServletResponse response) {
		return new PageRequestResponse(request, response);
	}

	protected boolean isFilter(final HttpServletRequest request) {
		/**
		 * 此函数需要子类覆盖
		 * 
		 * 排除一些不必要的资源(js, css, image等)
		 */
		final String rURI = request.getRequestURI().toLowerCase();
		if (rURI.indexOf("/$") > -1) {
			return rURI.lastIndexOf(".jsp") > 0;
		}
		if (endsWith(rURI, MATCHS_JS_CSS) || endsWith(rURI, MATCHS_IMAGE)
				|| endsWith(rURI, MATCHS_HTML)) {
			return false;
		}
		return true;
	}

	protected String[] MATCHS_JS_CSS = new String[] { ".js", ".css" };
	protected String[] MATCHS_IMAGE = new String[] { ".png", ".jpeg", ".jpg", ".gif", ".ico" };
	protected String[] MATCHS_HTML = new String[] { ".html", ".htm", ".txt" };

	protected boolean endsWith(final String str, final String[] matchs) {
		if (matchs != null) {
			for (final String m : matchs) {
				if (str.endsWith(m)) {
					return true;
				}
			}
		}
		return false;
	}

	protected String getRedirectUrl(final PageRequestResponse rRequest,
			final UrlForward urlForward) {
		return urlForward.getUrl();
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException {
		if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;
			final HttpServletResponse httpResponse = (HttpServletResponse) response;

			/* 是否进入过滤器, js,css,image等需要子类控制 */
			if (!isFilter(httpRequest)) {
				filterChain.doFilter(httpRequest, httpResponse);
				return;
			}

			/* 设置jsessionid */
			JsessionidUtils.setJSessionId(httpRequest);

			final PageRequestResponse rRequest = createPageRequestResponse(
					new PageRequest(httpRequest), httpResponse);
			try {
				/* 计时start */
				final boolean bHttpRequest = rRequest.isHttpRequest();
				if (bHttpRequest) {
					rRequest.setRequestAttr(MVCConst.PAGELOAD_TIME, System.currentTimeMillis());
					System.out.println("@HttpRequest Uri@"
							+ (rRequest.isLogin() ? rRequest.getLogin()
									: HttpUtils.getRemoteAddr(httpRequest))
							+ " => " + rRequest.getRequestURI());
				}

				/* page document */
				final PageDocument pageDocument = PageDocumentFactory.getPageDocument(rRequest);
				if (pageDocument == null) {
					if (doFilterInternal(rRequest, pageDocument) == EFilterResult.BREAK) {
						return;
					}

					filterChain.doFilter(rRequest.request, httpResponse);
				} else {
					final PageResponse _response = (PageResponse) (rRequest.response = new PageResponse(
							httpResponse, bHttpRequest));

					final PageParameter pp = PageParameter.get(rRequest, pageDocument);
					/* 以下为后处理部分 */
					if (doFilterInternal(pp, pageDocument) == EFilterResult.BREAK) {
						return;
					}

					IForward forward = null;
					final AbstractMVCPage abstractPage = pp.getPage();
					if (abstractPage != null) {
						forward = abstractPage.forward(pp);
						/* forward函数调用write，直接交给httpResponse并返回 */
						final String rHTML = _response.toString();
						if (rHTML != null) {
							write(pp, rHTML, getResponseCharset(rRequest));
							return;
						}
					}

					if (forward == null) {
						/* 产生 _response.toString() */
						filterChain.doFilter(pp.request, _response);
					}
					if (_response.isCommitted()) {
						return;
					}

					if (bHttpRequest) {
						// 重定向
						UrlForward urlForward;
						if (forward instanceof UrlForward
								&& (urlForward = (UrlForward) forward).isRedirect()) {
							pp.loc(getRedirectUrl(rRequest, urlForward));
							return;
						}
					}

					if (rRequest.isGzipResponse()) {
						_response.setGzipContentEncoding();
					}

					String rHTML = forward != null ? forward.getResponseText(pp) : _response.toString();
					// html解析并组合
					final boolean disabled = Convert.toBool(pp.getBeanProperty("disabled"));
					if (!disabled && (forward == null || forward.isHtmlParser())) {
						rHTML = new PageParser(pp).parser(rHTML).toHtml();
					}

					if (bHttpRequest) {
						// 写入cookies
						@SuppressWarnings("unchecked")
						final List<Cookie> cookies = (List<Cookie>) SessionCache
								.lremove(MVCConst.SESSION_ATTRI_COOKIES);
						if (cookies != null) {
							for (final Cookie cookie : cookies) {
								_response.addCookie(cookie);
							}
						}

						if (SessionCache.lget(MVCConst.SESSION_ATTRI_THROWABLE) != null) {
							rRequest.loc(getRedirectError(rRequest));
							return;
						}
					}

					if (forward instanceof JavascriptForward) {
						rHTML = "<div>" + JavascriptUtils.wrapScriptTag(rHTML) + "</div>";
					}
					/* 写入response */
					write(pp, rHTML, getResponseCharset(rRequest));
				}
			} catch (final Throwable e) {
				getLog().error(e);
				doThrowable(e, rRequest, getResponseCharset(rRequest));
			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private String getResponseCharset(final PageRequestResponse rRequest) {
		String rCharset = null;
		if (rRequest instanceof PageParameter) {
			rCharset = (String) ((PageParameter) rRequest)
					.getBeanProperty("responseCharacterEncoding");
		}
		if (!StringUtils.hasText(rCharset)) {
			rCharset = MVCContext.get().getMVCSettings().getCharset();
		}
		return rCharset;
	}

	protected EFilterResult doFilterInternal(final PageRequestResponse rRequest,
			final PageDocument pageDocument) throws IOException {
		if (SessionCache.lget(MVCConst.SESSION_ATTRI_THROWABLE) == null) {
			for (final IFilterListener listener : MVCContext.get().getFilterListeners()) {
				if (listener.doFilter(rRequest, pageDocument) == EFilterResult.BREAK) {
					return EFilterResult.BREAK;
				}
			}
		}
		return EFilterResult.SUCCESS;
	}

	protected void initResponse(final PageRequestResponse rRequest, final String charset) {
		rRequest.setResponseCharacterEncoding(charset);
		rRequest.setResponseContentType("text/html;charset=" + charset);
		// rRequest.setResponseNoCache();
	}

	protected void write(final PageRequestResponse rRequest, final String html, final String charset)
			throws IOException {
		initResponse(rRequest, charset);

		final HttpServletResponse _response = rRequest.response;
		// resetBuffer() 只会清掉內容的部份(Body)，而不会去清 status code 和 header
		_response.resetBuffer();
		final PrintWriter out = new PrintWriter(
				new OutputStreamWriter(_response.getOutputStream(), charset));
		if (_response instanceof PageResponse) {
			((PageResponse) _response).initOutputStream();
		}

		/* 计时end */
		Long l;
		if ((l = (Long) rRequest.getRequestAttr(MVCConst.PAGELOAD_TIME)) != null
				&& rRequest.isHttpRequest()) {
			final long pt = System.currentTimeMillis() - l.longValue();
			rRequest.setSessionAttr(MVCConst.PAGELOAD_TIME, pt); // 记录上一次
			rRequest.addCookie(MVCConst.PAGELOAD_TIME, pt / 1000d);
		}

		out.write(html);
		out.close();
	}

	protected void doThrowable(Throwable th, final PageRequestResponse rRequest,
			final String charset) throws IOException {
		th = MVCUtils.convertThrowable(th);
		if (rRequest.isAjaxRequest()) {
			final KVMap json = new KVMap().add("isJavascript", "true").add("rt",
					"$error(" + JsonUtils.toJSON(MVCUtils.createException(rRequest, th)) + ");");
			write(rRequest, JsonUtils.toJSON(json), charset);
		} else {
			if (rRequest.isHttpClientRequest()) {
				final StringBuilder sb = new StringBuilder("<div>");
				sb.append(HtmlConst.TAG_SCRIPT_START);
				if (rRequest.getBoolParameter(MVCConst.PARAM_PARENT_HTTP)) {
					SessionCache.lput(MVCConst.SESSION_ATTRI_THROWABLE, th);
					// 如果是HttpClient请求,则生成跳转脚本
					sb.append(JS.loc(getRedirectError(rRequest)));
				} else {
					sb.append("$error(");
					sb.append(JsonUtils.toJSON(MVCUtils.createException(rRequest, th))).append(");");
				}
				sb.append(HtmlConst.TAG_SCRIPT_END);
				sb.append("</div>");
				write(rRequest, sb.toString(), charset);
			} else {
				SessionCache.lput(MVCConst.SESSION_ATTRI_THROWABLE, th);
				rRequest.loc(getRedirectError(rRequest));
			}
		}
	}

	private String getRedirectError(final PageRequestResponse rRequest) throws IOException {
		final StringBuilder sb = new StringBuilder();
		rRequest.setSessionAttr("systemErrorPage",
				MVCContext.get().getMVCSettings().getErrorPath(rRequest));
		sb.append(MVCUtils.getPageResourcePath()).append("/jsp/error_template.jsp");
		return sb.toString();
	}

	@Override
	public void destroy() {
	}
}