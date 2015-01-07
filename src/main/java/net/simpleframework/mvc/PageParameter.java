package net.simpleframework.mvc;

import static net.simpleframework.common.I18n.$m;

import java.util.LinkedHashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.ctx.script.ScriptEvalFactory;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.component.ComponentException;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.IComponentHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageParameter extends PageRequestResponse {

	private IScriptEval scriptEval;

	private final PageDocument pageDocument;

	public PageParameter(final HttpServletRequest request, final HttpServletResponse response,
			final PageDocument pageDocument) {
		super(request, response);
		this.pageDocument = pageDocument;
	}

	public PageDocument getPageDocument() {
		return pageDocument;
	}

	public IPageResourceProvider getPageResourceProvider() {
		return getPageDocument().getPageResourceProvider();
	}

	public IScriptEval getScriptEval() {
		return scriptEval;
	}

	public IScriptEval createScriptEval() {
		scriptEval = ScriptEvalFactory.createDefaultScriptEval(MVCUtils.createVariables(this));
		return scriptEval;
	}

	public String getDocumentTitle() {
		return getPageDocument().getTitle(this);
	}

	public Map<String, AbstractComponentBean> getComponentBeans() {
		return getPageDocument().getComponentBeans(this);
	}

	public boolean hasComponentType(final Class<? extends AbstractComponentBean> beanClass) {
		for (final AbstractComponentBean componentBean : getComponentBeans().values()) {
			if (componentBean.getClass().equals(beanClass)) {
				return true;
			}
		}
		return false;
	}

	public void addComponentBean(final AbstractComponentBean componentBean) {
		final String componentName = ComponentParameter.get(this, componentBean).getComponentName();
		if (!StringUtils.hasText(componentName)) {
			throw ComponentException.of($m("PageDocument.1"));
		}
		getComponentBeans().put(componentName, componentBean);
	}

	public void removeComponentBean(final AbstractComponentBean componentBean) {
		final String componentName = ComponentParameter.get(this, componentBean).getComponentName();
		if (StringUtils.hasText(componentName)) {
			getComponentBeans().remove(componentName);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponentBean> T addComponentBean(final Map<String, Object> attriData,
			final Class<T> componentClass) {
		final T t = (T) AbstractComponentRegistry.getComponentRegistry(componentClass)
				.createComponentBean(this, attriData);
		addComponentBean(t);
		return t;
	}

	public <T extends AbstractComponentBean> T addComponentBean(final String name,
			final Class<T> componentClass) {
		return addComponentBean(new KVMap().add("name", name), componentClass);
	}

	public <T extends AbstractComponentBean> T addComponentBean(final Class<T> beanClass,
			final Class<? extends IComponentHandler> handlerClass) {
		final T t = addComponentBean(handlerClass.getSimpleName(), beanClass);
		t.setHandlerClass(handlerClass);
		return t;
	}

	public AbstractComponentBean getComponentBeanByName(final String name) {
		if (!StringUtils.hasText(name)) {
			return null;
		}
		AbstractComponentBean componentBean = getComponentBeans().get(name);
		if (componentBean == null) {
			componentBean = (AbstractComponentBean) SessionCache.lget(ComponentUtils
					.getComponentHashByName(getPageDocument(), name));
		}
		return componentBean;
	}

	public IPageHandler getPageHandler() {
		return getPageDocument().getPageHandler(this);
	}

	public PageBean getPageBean() {
		return getPageDocument().getPageBean();
	}

	public String getResourceHomePath(final Class<?> resourceClass) {
		return getPageResourceProvider().getResourceHomePath(resourceClass);
	}

	public String getResourceHomePath() {
		return getResourceHomePath(PageParameter.class);
	}

	public String getCssResourceHomePath(final Class<?> pageClass) {
		return getPageResourceProvider().getCssResourceHomePath(this, pageClass);
	}

	public String getCssResourceHomePath() {
		return getCssResourceHomePath(PageParameter.class);
	}

	public void addImportCSS(final String... importCSS) {
		addImportCSS((Class<?>) null, importCSS);
	}

	public void addImportCSS(final Class<?> pageClass, final String... importCSS) {
		if (importCSS == null || importCSS.length == 0) {
			return;
		}
		final LinkedHashSet<String> l = new LinkedHashSet<String>();
		final PageBean pageBean = getPageBean();
		final String[] oImportCSS = pageBean.getImportCSS();
		if (oImportCSS != null) {
			l.addAll(ArrayUtils.asList(oImportCSS));
		}
		for (String css : importCSS) {
			String prefix;
			if (pageClass != null && !css.startsWith(prefix = getCssResourceHomePath(pageClass))) {
				css = prefix + css;
			}
			l.add(css);
		}
		pageBean.setImportCSS(l.toArray(new String[l.size()]));
	}

	public void addImportJavascript(final String... importJavascript) {
		addImportJavascript((Class<?>) null, importJavascript);
	}

	public void addImportJavascript(final Class<?> pageClass, final String... importJavascript) {
		if (importJavascript == null || importJavascript.length == 0) {
			return;
		}
		final LinkedHashSet<String> l = new LinkedHashSet<String>();
		final PageBean pageBean = getPageBean();
		final String[] oImportJavascript = pageBean.getImportJavascript();
		if (oImportJavascript != null) {
			l.addAll(ArrayUtils.asList(oImportJavascript));
		}
		for (String js : importJavascript) {
			String prefix;
			if (pageClass != null && !js.startsWith(prefix = getResourceHomePath(pageClass))) {
				js = prefix + js;
			}
			l.add(js);
		}
		pageBean.setImportJavascript(l.toArray(new String[l.size()]));
	}

	public AbstractMVCPage getPage() {
		return (AbstractMVCPage) ObjectEx.singleton(getPageDocument().getPageClass());
	}

	public String hashId() {
		return getPageDocument().hashId();
	}

	public Object getBeanProperty(final String beanProperty) {
		final IPageHandler pageHandle = getPageHandler();
		if (pageHandle != null) {
			return pageHandle.getBeanProperty(this, beanProperty);
		} else {
			return BeanUtils.getProperty(getPageBean(), beanProperty);
		}
	}

	public static PageParameter get(final PageRequestResponse rRequest,
			final PageDocument pageDocument) {
		return get(rRequest.request, rRequest.response, pageDocument);
	}

	public static PageParameter get(final HttpServletRequest request,
			final HttpServletResponse response, final PageDocument pageDocument) {
		return new PageParameter(request, response, pageDocument);
	}

	// public <T> T
}
