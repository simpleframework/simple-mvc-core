package net.simpleframework.mvc.parser;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.simpleframework.common.StringUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.lib.org.jsoup.nodes.Node;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractContainerBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHtmlRender;
import net.simpleframework.mvc.component.IComponentRender;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class HtmlRender extends AbstractParser {
	void doTag(final PageParameter pp, final Element htmlHead, final Element element,
			final Map<String, AbstractComponentBean> componentBeans) {
		for (final Map.Entry<String, AbstractComponentBean> entry : componentBeans.entrySet()) {
			final AbstractComponentBean componentBean = entry.getValue();
			if (!(componentBean instanceof AbstractContainerBean)) {
				continue;
			}

			final ComponentParameter cp = ComponentParameter.get(pp, componentBean);
			if (!((Boolean) cp.getBeanProperty("runImmediately"))) {
				continue;
			}

			final String tagId = element.attr("id");
			if (!StringUtils.hasText(tagId)) {
				continue;
			}
			if (!tagId.equals(cp.getBeanProperty("containerId"))) {
				continue;
			}

			wrapElementStyle(cp, element);

			final IComponentRender render = componentBean.getComponentRegistry().getComponentRender();
			if (!(render instanceof IComponentHtmlRender)) {
				continue;
			}

			doBeforeRender(cp);
			final String html = ((IComponentHtmlRender) render).getHtml(cp);
			if (!StringUtils.hasText(html)) {
				return;
			}

			final List<Node> nodes = ParserUtils.htmlToNodes(pp, html, htmlHead);

			element.empty();

			for (final Node child : nodes) {
				if (child instanceof Element) {
					final Element ele = (Element) child;
					final String tagName = ele.tagName();
					boolean body;
					if ((body = "body".equalsIgnoreCase(tagName)) || "head".equalsIgnoreCase(tagName)) {
						if (ele.childNodeSize() == 0) {
							continue;
						}
						if (body) {
							for (final Node child2 : ele.childNodesCopy()) {
								element.appendChild(child2);
							}
							continue;
						}
					}
				}
				element.appendChild(child);
			}
		}
	}

	private void wrapElementStyle(final ComponentParameter cp, final Element element) {
		final String width = (String) cp.getBeanProperty("width");
		final String height = (String) cp.getBeanProperty("height");
		final StringBuilder style = new StringBuilder();
		if (StringUtils.hasText(width)) {
			style.append("width:").append(width);
			if (style.charAt(style.length() - 1) != ';') {
				style.append(";");
			}
		}
		if (StringUtils.hasText(height)) {
			style.append("height:").append(height);
		}

		if (style.length() > 0) {
			final String style2 = element.attr("style");
			if (StringUtils.hasText(style2)) {
				final Set<String> set = AbstractElement.toSet(style2);
				set.addAll(AbstractElement.toSet(style.toString()));
				element.attr("style", StringUtils.join(set, ";"));
			} else {
				element.attr("style", style.toString());
			}
		}
	}
}
