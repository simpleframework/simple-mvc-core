package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.Collection;

import eu.bitwalker.useragentutils.OperatingSystem;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.common.element.Meta;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCHtmlBuilder extends ObjectEx implements IMVCSettingsAware {

	public final static String HTML5_DOC_TYPE = "<!DOCTYPE HTML>";

	public final static String HTML401_DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";

	public final static String XHTML10_DOC_TYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";

	public String doHttpRequestDoctype(final PageParameter pp) {
		return HTML5_DOC_TYPE;
	}

	public Collection<Meta> doHttpRequestMeta(final PageParameter pp) {
		final ArrayList<Meta> coll = new ArrayList<Meta>();
		if (pp.isIE8(">=") != null) {
			coll.add(Meta.DEFAULT_COMPATIBLE);
		}
		coll.add(Meta.RENDERER_WEBKIT);
		coll.add(Meta.CLEARTYPE);
		coll.add(Meta.GOOGLE_NOTRANSLATE);
		final AbstractMVCPage page = pp.getPage();
		coll.add(Meta.contentType("text/html; charset="
				+ (page != null ? page.getResponseCharset() : mvcSettings.getCharset())));
		if (page != null) {
			page.onHttpRequestMeta(pp, coll);
		}
		return coll;
	}

	public String doHttpRequestCSS(final PageParameter pp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("body, body * { font-family: Verdana,");
		final OperatingSystem os = pp.getUserAgentUtils().getOperatingSystem();
		final OperatingSystem group = os.getGroup();
		if (group == OperatingSystem.WINDOWS) {
			if (os.getId() > OperatingSystem.WINDOWS_XP.getId()) {
				sb.append("'Microsoft YaHei',");
			}
		} else if (group == OperatingSystem.IOS) {
			sb.append("'PingFang',");
		}
		sb.append("Helvetica,Arial,Sans-Serif");
		sb.append("; }");
		AbstractMVCPage page;
		if ((page = pp.getPage()) != null) {
			page.onHttpRequestCSS(pp, sb);
		}
		return sb.toString();
	}

	public void doHtmlNormalise(final PageParameter pp, final Element element) {
		final AbstractMVCPage page = pp.getPage();
		if (page != null) {
			page.onHtmlNormalise(pp, element);
		}
	}
}
