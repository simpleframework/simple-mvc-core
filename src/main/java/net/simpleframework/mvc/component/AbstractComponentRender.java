package net.simpleframework.mvc.component;

import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.th.NotImplementedException;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.html.HtmlConst;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.UrlForward;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentRender extends ObjectEx implements IComponentRender,
		HtmlConst {
	private final IComponentRegistry componentRegistry;

	public AbstractComponentRender(final IComponentRegistry componentRegistry) {
		this.componentRegistry = componentRegistry;
	}

	@Override
	public IComponentRegistry getComponentRegistry() {
		return componentRegistry;
	}

	public static abstract class ComponentJavascriptRender extends AbstractComponentRender implements
			IComponentJavascriptRender {

		public ComponentJavascriptRender(final IComponentRegistry componentRegistry) {
			super(componentRegistry);
		}
	}

	public static abstract class ComponentHtmlRender extends AbstractComponentRender implements
			IComponentHtmlRender {
		public ComponentHtmlRender(final IComponentRegistry componentRegistry) {
			super(componentRegistry);
		}

		@Override
		public String getHtml(final ComponentParameter cp) {
			final IForward forward = getResponseForward(cp);
			if (forward instanceof UrlForward) {
				((UrlForward) forward).setIncludeRequestData((String) cp
						.getBeanProperty("includeRequestData"));
			}
			return forward.getResponseText(cp);
		}

		public IForward getResponseForward(final ComponentParameter cp) {
			final String url = ComponentUtils.getResourceHomePath(cp.componentBean.getClass())
					+ HttpUtils.addParameters(getRelativePath(cp),
							(String) cp.getBeanProperty("parameters"));
			return new UrlForward(url);
		}

		protected String getRelativePath(final ComponentParameter cp) {
			throw NotImplementedException.of(getClass(), "getRelativePath");
		}
	}
}