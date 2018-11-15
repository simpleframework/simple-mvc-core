package net.simpleframework.mvc.parser;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.lib.org.jsoup.nodes.Node;
import net.simpleframework.lib.org.jsoup.select.Elements;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IPageResourceProvider;
import net.simpleframework.mvc.MVCConst;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentRegistryFactory;
import net.simpleframework.mvc.component.IComponentRegistry;
import net.simpleframework.mvc.component.IComponentResourceProvider;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ResourceBinding extends AbstractParser {

	public void doTag(final PageParameter pp, final Element htmlHead,
			final Map<String, AbstractComponentBean> componentBeans) {
		final PageDocument pageDocument = pp.getPageDocument();
		final IPageResourceProvider prp = pageDocument.getPageResourceProvider();
		final AbstractMVCPage pageInstance = pp.getPage();
		if (pp.isHttpRequest()) {
			String headScript;
			if (pageInstance != null
					&& StringUtils.hasText(headScript = pageInstance.initHeadScript(pp))) {
				ParserUtils.addScriptText(htmlHead, headScript);
			}

			// base
			String[] cssArr = null;
			if (pageInstance != null) {
				cssArr = pageInstance.getDefaultCssPath(pp);
			}
			if (cssArr == null) {
				cssArr = prp.getCssPath(pp);
			}
			if (cssArr != null) {
				for (final String css : cssArr) {
					ParserUtils.addStylesheet(pp, htmlHead, css);
				}
			}
			String[] jsArr = null;
			if (pageInstance != null) {
				jsArr = pageInstance.getDefaultJavascriptPath(pp);
			}
			if (jsArr == null) {
				jsArr = prp.getJavascriptPath(pp);
			}
			if (jsArr != null) {
				for (final String js : jsArr) {
					ParserUtils.addScriptSRC(pp, htmlHead, js);
				}
			}
		}

		// component
		final Set<String> keys = new LinkedHashSet<>();
		for (final AbstractComponentBean componentBean : componentBeans.values()) {
			keys.add(componentBean.getComponentRegistry().getComponentName());
		}

		final String pageHome = prp.getResourceHomePath();
		final ComponentRegistryFactory factory = ComponentRegistryFactory.get();
		for (final String componentName : keys) {
			final IComponentRegistry registry = factory.getComponentRegistry(componentName);
			final IComponentResourceProvider crp = registry.getComponentResourceProvider();
			if (crp == null) {
				continue;
			}
			doDependentComponents(pp, htmlHead, crp.getDependentComponents(pp));
			doComponentResource(htmlHead, pp, crp, pageHome);
		}

		// 页面依赖的组件
		if (pageInstance != null) {
			doDependentComponents(pp, htmlHead, pageInstance.getDependentComponents(pp));
		}

		// 转换UrlForward直接输出的代码
		final Elements elements = htmlHead.ownerDocument().select("." + MVCConst.HTML_BASE64_CLASS);
		for (int i = 0; i < elements.size(); i++) {
			final Element element = elements.get(i);
			final Element parent = element.parent();
			final List<Node> nodes = ParserUtils.htmlToNodes(pp,
					new String(AlgorithmUtils.base64Decode(element.text())), htmlHead);
			final int jj = element.elementSiblingIndex();
			element.remove();
			parent.insertChildren(jj, nodes);
		}

		// page css
		final Collection<String> cssColl = pageDocument.getImportCSS(pp);
		if (cssColl != null) {
			for (String css : cssColl) {
				if (!HttpUtils.isAbsoluteUrl(css)) {
					if (!css.startsWith("/")) {
						css = pageHome + "/" + css;
					} else {
						css = pp.wrapContextPath(css);
					}
				}
				ParserUtils.addStylesheet(pp, htmlHead, css);
			}
		}

		// page js
		final Collection<String> jsColl = pageDocument.getImportJavascript(pp);
		if (jsColl != null) {
			for (String js : jsColl) {
				if (!HttpUtils.isAbsoluteUrl(js)) {
					if (!js.startsWith("/")) {
						js = pageHome + "/" + js;
					} else {
						js = pp.wrapContextPath(js);
					}
				}
				ParserUtils.addScriptSRC(pp, htmlHead, js);
			}
		}
	}

	private void doDependentComponents(final PageParameter pp, final Element htmlHead,
			final String[] dependents) {
		if (dependents == null || dependents.length == 0) {
			return;
		}
		final ComponentRegistryFactory factory = ComponentRegistryFactory.get();
		final String pageHome = pp.getPageResourceProvider().getResourceHomePath();
		for (final String dependent : dependents) {
			final IComponentRegistry registry2 = factory.getComponentRegistry(dependent);
			if (registry2 == null) {
				continue;
			}
			final IComponentResourceProvider crp2 = registry2.getComponentResourceProvider();
			if (crp2 == null) {
				continue;
			}
			doComponentResource(htmlHead, pp, crp2, pageHome);
		}
	}

	private void doComponentResource(final Element htmlHead, final PageParameter pp,
			final IComponentResourceProvider crp, final String pageHome) {
		final String[] cssArr = crp.getCssPath(pp);
		if (cssArr != null) {
			for (final String css : cssArr) {
				ParserUtils.addStylesheet(pp, htmlHead, css);
			}
		}
		final String[] jsArr = crp.getJavascriptPath(pp);
		if (jsArr != null) {
			for (final String js : jsArr) {
				ParserUtils.addScriptSRC(pp, htmlHead, js);
			}
		}
	}
}
