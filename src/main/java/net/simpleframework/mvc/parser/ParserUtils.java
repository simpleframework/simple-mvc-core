package net.simpleframework.mvc.parser;

import java.util.ArrayList;
import java.util.List;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.lib.org.jsoup.nodes.DataNode;
import net.simpleframework.lib.org.jsoup.nodes.Document;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.lib.org.jsoup.nodes.Node;
import net.simpleframework.lib.org.jsoup.select.Elements;
import net.simpleframework.mvc.MVCContext;
import net.simpleframework.mvc.PageParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ParserUtils {
	private final static String SCRIPT_TYPE = "text/javascript";

	static Element addScriptSRC(final PageParameter pp, final Element element, String src) {
		if (!StringUtils.hasText(src)) {
			return null;
		}
		final Elements scripts;
		int size;
		final int p = src.indexOf("?");
		if (p > 0) { // 含有参数
			scripts = element.select("script[src=" + src + "]"); // 全部相等
			size = scripts.size();
		} else {
			scripts = element.select("script[src^=" + src + "]");
			size = scripts.size();
			if (size == 0) {
				src = HttpUtils.addParameters(src, "v=" + MVCContext.get().getVersion());
			}
		}
		if (size == 0) {
			return element.appendElement("script").attr("type", SCRIPT_TYPE).attr("src", src);
		} else {
			return scripts.first();
		}
	}

	static Element addStylesheet(final PageParameter pp, final Element element, String href) {
		if (!StringUtils.hasText(href)) {
			return null;
		}
		final Elements links;
		int size;
		final int p = href.indexOf("?");
		if (p > 0) {
			links = element.select("link[href=" + href + "]");
			size = links.size();
		} else {
			links = element.select("link[href^=" + href + "]");
			size = links.size();
			if (size == 0) {
				href = HttpUtils.addParameters(href, "v=" + MVCContext.get().getVersion());
			}
		}
		if (size == 0) {
			return element.appendElement("link").attr("rel", "stylesheet").attr("type", "text/css")
					.attr("href", href);
		} else {
			return links.first();
		}
	}

	static Element addScriptText(final Element element, final String js) {
		return addScriptText(element, js, true);
	}

	static Element addScriptText(final Element element, String js, final boolean compress) {
		js = StringUtils.blank(js);
		return element
				.appendElement("script")
				.attr("type", SCRIPT_TYPE)
				.appendChild(
						new DataNode(compress ? JavascriptUtils.jsCompress(js) : js, element.baseUri()));
	}

	static List<Node> htmlToNodes(final PageParameter pp, final String html, final Element htmlHead) {
		final Document htmlDocument = HtmlUtils.createHtmlDocument(html, "html");
		for (final Element moveHead : htmlDocument.select("head[move]")) {
			for (final Element link : moveHead.select("link[href], link[rel=stylesheet]")) {
				addStylesheet(pp, htmlHead, link.attr("href"));
				link.remove();
			}
			final StringBuilder jsCode = new StringBuilder();
			for (final Element script : moveHead.select("script")) {
				final String src = script.attr("src");
				if (StringUtils.hasText(src)) {
					addScriptSRC(pp, htmlHead, src);
				} else {
					jsCode.append(StringUtils.blank(script.data()));
				}
				script.remove();
			}
			if (jsCode.length() > 0) {
				addScriptText(htmlHead, jsCode.toString());
			}
			if (moveHead.children().size() == 0) {
				moveHead.remove();
			}
		}
		final Element body = htmlDocument.body();
		return new ArrayList<Node>(body != null ? body.childNodes() : htmlDocument.childNodes());
	}
}
