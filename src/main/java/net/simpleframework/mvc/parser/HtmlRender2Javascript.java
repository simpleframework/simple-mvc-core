package net.simpleframework.mvc.parser;

import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHtmlRender;
import net.simpleframework.mvc.component.IComponentRender;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class HtmlRender2Javascript extends AbstractParser {

	void doTag(final PageParameter pp, final Element htmlHead,
			final Map<String, AbstractComponentBean> componentBeans) {
		for (final Map.Entry<String, AbstractComponentBean> entry : componentBeans.entrySet()) {
			final AbstractComponentBean componentBean = entry.getValue();

			final IComponentRender render = componentBean.getComponentRegistry().getComponentRender();
			if (!(render instanceof IComponentHtmlRender)) {
				continue;
			}

			final ComponentParameter cp = ComponentParameter.get(pp, componentBean);
			final String js = ((IComponentHtmlRender) render).getHtmlJavascriptCode(cp);
			if (StringUtils.hasText(js)) {
				// 立即执行，此处不用wrapWhenReady
				// 该js要优先于HttpClient产生的jscode
				ParserUtils.addScriptText(htmlHead, JavascriptUtils.wrapFunction(js));
			}
		}
	}
}
