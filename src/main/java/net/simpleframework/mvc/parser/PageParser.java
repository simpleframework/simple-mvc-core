package net.simpleframework.mvc.parser;

import static net.simpleframework.common.I18n.$m;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.I18n;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.th.ParserException;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.lib.org.jsoup.nodes.DataNode;
import net.simpleframework.lib.org.jsoup.nodes.Document;
import net.simpleframework.lib.org.jsoup.nodes.DocumentType;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.lib.org.jsoup.nodes.Node;
import net.simpleframework.lib.org.jsoup.nodes.TextNode;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IPageHandler;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.MVCContext;
import net.simpleframework.mvc.MVCException;
import net.simpleframework.mvc.MVCHtmlBuilder;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.Meta;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentException;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public final class PageParser extends AbstractPageParser {

	private static final MVCHtmlBuilder htmlBuilder = MVCContext.get().getPageHtmlBuilder();

	private Document htmlDoc;

	private Element headElement;

	public PageParser(final PageParameter pp) {
		super(pp);
	}

	public PageParser parser(final String responseString) {
		try {
			final PageParameter pp = getPageParameter();
			final IPageHandler pageHandle = pp.getPageHandler();
			if (pageHandle != null) {
				pageHandle.onBeforeComponentRender(pp);
			}

			beforeCreate(responseString);

			final Map<String, AbstractComponentBean> oComponentBeans = pp.getComponentBeans();
			for (final AbstractComponentBean componentBean : oComponentBeans.values()) {
				if (MVCContext.get().getComponentBeanByHashId(pp, componentBean.hashId()) == null) {
					throw ComponentException.of($m("ComponentParameter.0"));
				}
			}

			resourceBinding.doTag(pp, headElement, oComponentBeans);
			normaliseNode(pp, htmlDoc, oComponentBeans);
			javascriptRender.doTag(pp, headElement, oComponentBeans);

			// 执行handle
			if (pageHandle != null) {
				final KVMap dataBinding = new KVMap() {
					@Override
					public Object put(final String key, Object value) {
						if (value instanceof Enum) {
							value = ((Enum<?>) value).ordinal();
						}
						return super.put(key, value);
					}

					private static final long serialVersionUID = 971286039035769475L;
				};
				final PageSelector selector = new PageSelector();
				doPageLoad(pageHandle, dataBinding, selector);
				pageLoaded.doTag(pp, headElement, dataBinding, selector);
			}
		} catch (final Exception e) {
			throw ParserException.of(e);
		}
		return this;
	}

	private void beforeCreate(final String responseString) {
		if (pp.isHttpRequest()) {
			htmlDoc = HtmlUtils.parseDocument(responseString);
			headElement = htmlDoc.head();
			// title
			final String title = pp.getDocumentTitle();
			if (StringUtils.hasText(title)) {
				htmlDoc.title(I18n.replaceI18n(title));
			}
			// meta
			final Collection<Meta> coll = htmlBuilder.doHttpRequestMeta(pp);
			if (coll != null) {
				for (final Meta attri : coll) {
					headElement.append(attri.toString());
				}
			}
			// favicon
			final String favicon = (String) pp.getBeanProperty("favicon");
			if (StringUtils.hasText(favicon)) {
				headElement.appendElement("link").attr("type", "image/x-icon")
						.attr("rel", "SHORTCUT ICON").attr("href", pp.wrapContextPath(favicon));
			}

			final String css = htmlBuilder.doHttpRequestCSS(pp);
			if (StringUtils.hasText(css)) {
				headElement
						.appendChild(htmlDoc.createElement("style").attr("type", "text/css").text(css));
			}
		} else {
			htmlDoc = HtmlUtils.createHtmlDocument(responseString);
			headElement = htmlDoc.select("head").first();
			if (headElement == null) {
				headElement = htmlDoc.createElement("head");
				htmlDoc.prependChild(headElement);
			}
			headElement.attr("move", "true");
		}
	}

	private static final String[] I18N_ATTRIBUTES = new String[] { "value", "title", "placeholder" };

	private static final String[] CONTEXTPATH_ATTRIBUTES = HtmlUtils.CONTEXTPATH_ATTRIBUTES;

	private void normaliseNode(final PageParameter pp, final Element element,
			final Map<String, AbstractComponentBean> componentBeans) {
		htmlBuilder.doHtmlNormalise(pp, element);
		final Element head = htmlDoc.head();
		for (final Node child : element.childNodes()) {
			if (child instanceof Element) {
				final String id = child.attr("id");
				if (StringUtils.hasText(id)) {
					htmlRender.doTag(pp, head, (Element) child, componentBeans);
				}

				for (final String attri : I18N_ATTRIBUTES) {
					final String sVal = child.attr(attri);
					if (StringUtils.hasText(sVal)) {
						child.attr(attri, I18n.replaceI18n(sVal));
					}
				}
				for (final String attri : CONTEXTPATH_ATTRIBUTES) {
					final String sVal = child.attr(attri);
					if (StringUtils.hasText(sVal)) {
						child.attr(attri, pp.wrapContextPath(sVal));
					}
				}

				final String nodeName = child.nodeName();
				if ("a".equalsIgnoreCase(nodeName)) {
					child.attr("hidefocus", "hidefocus");
				} else if ("form".equalsIgnoreCase(nodeName)
						&& !StringUtils.hasText(child.attr("action"))) {
					child.attr("action", "javascript:void(0);").attr("autocomplete", "off");
				} else if ("select".equalsIgnoreCase(nodeName)) {
					if (child.hasAttr("readonly")) {
						final Element opt = ((Element) child).getElementsByAttribute("selected").first();
						if (opt != null) {
							child.replaceWith(htmlDoc.createElement("span").addClass("readonly")
									.appendText(opt.text()));
						}
					}
				} else if ("img".equalsIgnoreCase(nodeName)) {
					child.attr("ondragstart", "return false;");
				}

				normaliseNode(pp, (Element) child, componentBeans);
			} else if (child instanceof TextNode) {
				final String text = ((TextNode) child).getWholeText();
				if (StringUtils.hasText(text)) {
					((TextNode) child).text(I18n.replaceI18n(text));
				}
			} else if (child instanceof DataNode) {
				final String text = ((DataNode) child).getWholeData();
				if (StringUtils.hasText(text)) {
					child.attr("data", I18n.replaceI18n(text));
				}
			}
		}
	}

	private void doPageLoad(final IPageHandler pageHandle, final Map<String, Object> dataBinding,
			final PageSelector selector) {
		final String handlerMethod = pp.getPageBean().getHandlerMethod();
		if (StringUtils.hasText(handlerMethod) && !(pageHandle instanceof AbstractMVCPage.PageLoad)) {
			try {
				final Method methodObject = pageHandle.getClass().getMethod(handlerMethod,
						PageParameter.class, Map.class, PageSelector.class);
				ClassUtils.invoke(methodObject, pageHandle, pp, dataBinding, selector);
			} catch (final NoSuchMethodException e) {
				throw MVCException.of(e);
			}
		} else {
			try {
				pageHandle.onPageLoad(pp, dataBinding, selector);
			} catch (final Exception e) {
				throw MVCException.of(e);
			}
		}
	}

	public String toHtml() {
		if (htmlDoc == null) {
			return "";
		}
		String html = htmlDoc.html();
		if (pp.isHttpRequest()) {
			boolean doctype = false;
			for (final Node child : htmlDoc.childNodes()) {
				if (child instanceof DocumentType) {
					doctype = true;
					break;
				}
			}
			if (!doctype) {
				html = htmlBuilder.doHttpRequestDoctype(getPageParameter()) + html;
			}
		}
		return html;
	}

	private static ResourceBinding resourceBinding = new ResourceBinding();
	private static HtmlRender htmlRender = new HtmlRender();
	private static JavascriptRender javascriptRender = new JavascriptRender();
	private static PageLoaded pageLoaded = new PageLoaded();
}
