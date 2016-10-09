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

	protected boolean isFilter(final HttpServletRequest request) {
		/**
		 * 此函数需要子类覆盖
		 * 
		 * 排除一些不必要的资源(js, css, image等)
		 */
		final String rURI = request.getRequestURI();
		if (rURI.indexOf("/$") > -1) {
			return rURI.lastIndexOf(".jsp") > 0;
		}
		if (rURI.endsWith(".html") || rURI.endsWith(".htm") || rURI.endsWith(".txt")) {
			return false;
		}
		return true;
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

			final PageRequestResponse rRequest = new PageRequestResponse(new PageRequest(httpRequest),
					httpResponse);
			try {
				/* 计时start */
				final boolean bHttpRequest = rRequest.isHttpRequest();
				if (bHttpRequest) {
					rRequest.setRequestAttr(MVCConst.PAGELOAD_TIME, System.currentTimeMillis());
				}

				/* page document */
				final PageDocument pageDocument = PageDocumentFactory.getPageDocument(rRequest);
				if (pageDocument == null) {
					if (doFilterInternal(rRequest, filterChain) == EFilterResult.BREAK) {
						return;
					}

					filterChain.doFilter(rRequest.request, httpResponse);
				} else {
					final PageResponse _response = (PageResponse) (rRequest.response = new PageResponse(
							httpResponse, bHttpRequest));
					if (rRequest.isGzipResponse()) {
						_response.setGzipContentEncoding();
					}

					final PageParameter pp = PageParameter.get(rRequest, pageDocument);
					/* 以下为后处理部分 */
					if (doFilterInternal(pp, filterChain) == EFilterResult.BREAK) {
						return;
					}

					IForward forward = null;
					final AbstractMVCPage abstractPage = pp.getPage();
					if (abstractPage != null) {
						forward = abstractPage.forward(pp);
						/* forward函数调用write，直接交给httpResponse并返回 */
						String rHTML = _response.toString();
						if (rHTML == null) {
							if (forward instanceof JavascriptForward) {
								rHTML = JavascriptUtils.wrapScriptTag(forward.toString());
							}
						}
						if (rHTML != null) {
							final PrintWriter writer = httpResponse.getWriter();
							writer.write(rHTML);
							writer.close();
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
						UrlForward uf;
						if (forward instanceof UrlForward && (uf = (UrlForward) forward).isRedirect()) {
							pp.loc(uf.getUrl());
							return;
						}
					}

					String rHTML = forward != null ? forward.getResponseText(pp) : _response.toString();
					// html解析并组合
					if (!Convert.toBool(pp.getBeanProperty("disabled"))
							&& (forward == null || forward.isHtmlParser())) {
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
			final FilterChain filterChain) throws IOException {
		if (SessionCache.lget(MVCConst.SESSION_ATTRI_THROWABLE) == null) {
			for (final IFilterListener listener : MVCContext.get().getFilterListeners()) {
				if (listener.doFilter(rRequest, filterChain) == EFilterResult.BREAK) {
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
			SessionCache.lput(MVCConst.SESSION_ATTRI_THROWABLE, th);
			if (rRequest.isHttpClientRequest()) {
				// 如果是HttpClient请求,则生成跳转脚本
				final StringBuilder sb = new StringBuilder();
				sb.append(HtmlConst.TAG_SCRIPT_START);
				sb.append(JS.loc(getRedirectError(rRequest)));
				sb.append(HtmlConst.TAG_SCRIPT_END);
				write(rRequest, sb.toString(), charset);
			} else {
				rRequest.loc(getRedirectError(rRequest));
			}
		}
	}

	private String getRedirectError(final PageRequestResponse rRequest) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append(MVCUtils.getPageResourcePath()).append("/jsp/error_template.jsp?systemErrorPage=");
		sb.append(MVCContext.get().getMVCSettings().getErrorPath(rRequest));
		return sb.toString();
	}

	@Override
	public void destroy() {
	}
}