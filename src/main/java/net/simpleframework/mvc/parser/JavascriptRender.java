package net.simpleframework.mvc.parser;

import java.util.Map;

import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentJavascriptRender;
import net.simpleframework.mvc.component.IComponentRender;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class JavascriptRender extends AbstractParser {

	void doTag(final PageParameter pp, final Element htmlHead,
			final Map<String, AbstractComponentBean> componentBeans) {
		final StringBuilder sb = new StringBuilder();
		for (final Map.Entry<String, AbstractComponentBean> entry : componentBeans.entrySet()) {
			final AbstractComponentBean componentBean = entry.getValue();

			final IComponentRender render = componentBean.getComponentRegistry().getComponentRender();
			if (render instanceof IComponentJavascriptRender) {
				final ComponentParameter cp = ComponentParameter.get(pp, componentBean);
				doBeforeRender(cp);
				final String scriptCode = ((IComponentJavascriptRender) render).getJavascriptCode(cp);
				if (scriptCode != null) {
					sb.append(JavascriptUtils.wrapFunction(scriptCode));
				}
			}
		}

		if (sb.length() > 0) {
			ParserUtils.addScriptText(htmlHead, sb.toString());
		}
	}
}
