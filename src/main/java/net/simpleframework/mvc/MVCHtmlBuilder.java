package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.Collection;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.common.element.Meta;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCHtmlBuilder extends ObjectEx implements IMVCContextVar {

	public final static String HTML5_DOC_TYPE = "<!DOCTYPE HTML>";

	public final static String HTML401_DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";

	public final static String XHTML10_DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";

	public String doctype(final PageParameter pp) {
		return HTML5_DOC_TYPE;
	}

	public String css(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		final AbstractMVCPage page = pp.getPage();
		String css = null;
		if (page != null) {
			css = page.html_css(pp);
		}
		if (css == null) {
			sb.append("body, body * { font-family: Verdana,");
			if (pp.getUserAgent().indexOf("Windows NT 6.") > -1) {
				sb.append("'Microsoft YaHei','\u5fae\u8f6f\u96c5\u9ed1',");
			} else {
				sb.append("SimSun,");
			}
			sb.append("Sans-Serif,Tahoma,Arial");
			sb.append("; }");
		}
		return sb.toString();
	}

	public Collection<Meta> meta(final PageParameter pp) {
		final ArrayList<Meta> al = new ArrayList<Meta>();
		final AbstractMVCPage page = pp.getPage();
		boolean _contentType = true;
		boolean _refresh = true;
		boolean _compatible = true;
		Collection<Meta> _coll;
		if (page != null && (_coll = page.html_meta(pp)) != null) {
			for (final Meta _meta : _coll) {
				al.add(_meta);
				if ("Content-Type".equalsIgnoreCase(_meta.getHttpEquiv())) {
					_contentType = false;
				}
				if ("refresh".equalsIgnoreCase(_meta.getHttpEquiv())) {
					_refresh = true;
				}
				if ("X-UA-Compatible".equalsIgnoreCase(_meta.getHttpEquiv())) {
					_compatible = false;
				}
			}
		}
		if (_contentType) {
			al.add(Meta.contentType("text/html; charset="
					+ (page != null ? page.getChartset() : settings.getCharset())));
		}
		String redirectUrl;
		if (_refresh && page != null && StringUtils.hasText(redirectUrl = page.getRedirectUrl(pp))) {
			al.add(new Meta("refresh", "0;url=" + pp.wrapContextPath(redirectUrl)));
		}
		if (_compatible && pp.getIEVersion() != null) {
			al.add(Meta.DEFAULT_COMPATIBLE);
		}
		return al;
	}

	public void normalise(final PageParameter pp, final Element element) {
		final AbstractMVCPage page = pp.getPage();
		if (page != null) {
			page.html_normalise(pp, element);
		}
	}
}
