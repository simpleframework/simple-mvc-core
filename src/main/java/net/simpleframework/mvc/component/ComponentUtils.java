package net.simpleframework.mvc.component;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IMVCSettingsAware;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageDocumentFactory;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ComponentUtils implements IMVCSettingsAware {

	public static String getResourceHomePath(
			final Class<? extends AbstractComponentBean> componentBeanClass) {
		return AbstractComponentRegistry.getComponentRegistry(componentBeanClass)
				.getComponentResourceProvider().getResourceHomePath();
	}

	public static String getCssResourceHomePath(final ComponentParameter cp) {
		final Class<? extends AbstractComponentBean> componentClass = cp.componentBean.getClass();
		return AbstractComponentRegistry.getComponentRegistry(componentClass)
				.getComponentResourceProvider().getCssResourceHomePath(cp, componentClass);
	}

	public static String getCssResourceHomePath(final PageParameter pp,
			final Class<? extends AbstractComponentBean> componentBeanClass) {
		return AbstractComponentRegistry.getComponentRegistry(componentBeanClass)
				.getComponentResourceProvider().getCssResourceHomePath(pp);
	}

	public static Map<String, AbstractComponentBean> allComponentsCache;
	static {
		allComponentsCache = new ConcurrentHashMap<>();
	}

	public static AbstractComponentBean getComponent(final String hashId) {
		return allComponentsCache.get(hashId);
	}

	public static void putComponent(final AbstractComponentBean componentBean) {
		allComponentsCache.put(componentBean.hashId(), componentBean);
	}

	public static void removeComponent(final String hashId) {
		allComponentsCache.remove(hashId);
	}

	public static String getComponentHashByName(final PageDocument pageDocument,
			final String componentName) {
		return ObjectUtils.hashStr(pageDocument.hashId() + componentName);
	}

	public static AbstractComponentBean getComponentBeanByName(final PageRequestResponse rRequest,
			final String xmlpath, final String componentName) {
		final PageDocument pageDocument = PageDocumentFactory.getPageDocumentByPath(rRequest,
				xmlpath);
		return pageDocument != null
				? PageParameter.get(rRequest, pageDocument).getComponentBeanByName(componentName)
				: null;
	}

	/*--------------------------------- handle -----------------------------------*/

	public static String REQUEST_HANDLER_KEY = "@handlerClass_";

	public static Map<String, Object> toFormParameters(final ComponentParameter cp) {
		final KVMap _parameters = new KVMap();
		Map<String, Object> parameters2;
		if ((parameters2 = cp.getFormParameters()) != null) {
			_parameters.putAll(parameters2);
		}
		final IComponentHandler hdl = cp.getComponentHandler();
		if (hdl != null && (parameters2 = hdl.getFormParameters(cp)) != null) {
			_parameters.putAll(parameters2);
		}

		final KVMap parameters = new KVMap();
		if (_parameters.size() > 0) {
			Object val;
			for (final String k : mvcSettings.getSystemParamKeys()) {
				_parameters.remove(k);
			}
			for (final Map.Entry<String, Object> entry : _parameters.entrySet()) {
				if ((val = entry.getValue()) != null) {
					parameters.put(entry.getKey(), val);
				}
			}
		}
		return parameters;
	}

	public static IComponentHandler getComponentHandler(final PageRequestResponse rRequest,
			final AbstractComponentBean componentBean) {
		String stringClass = componentBean.getHandlerClass();
		if (!StringUtils.hasText(stringClass)) {
			return null;
		}
		Class<?> handlerClass;
		try {
			handlerClass = ClassUtils.forName(stringClass);
			if (ObjectFactory.isAbstract(handlerClass)) {
				throw ComponentHandlerException.of($m("ComponentUtils.0"));
			}
		} catch (final ClassNotFoundException e) {
			throw ComponentHandlerException.of(e);
		}

		IComponentHandler hdl = null;
		final EComponentHandlerScope handleScope = componentBean.getHandleScope();
		if (handleScope == EComponentHandlerScope.singleton) {
			if (AbstractMVCPage.class.isAssignableFrom(handlerClass)) {
				hdl = ((AbstractMVCPage) ObjectFactory.singleton(handlerClass))
						.createComponentHandler(ComponentParameter.get(rRequest, componentBean));
			} else {
				hdl = (IComponentHandler) ObjectFactory.singleton(handlerClass);
			}
		} else if (handleScope == EComponentHandlerScope.prototype) {
			if (AbstractMVCPage.class.isAssignableFrom(handlerClass)) {
				stringClass += "#" + componentBean.getComponentRegistry().getComponentName();
			}
			stringClass = REQUEST_HANDLER_KEY + stringClass;
			hdl = (IComponentHandler) rRequest.getRequestAttr(stringClass);
			if (hdl == null) {
				if (AbstractMVCPage.class.isAssignableFrom(handlerClass)) {
					hdl = ((AbstractMVCPage) ObjectFactory.singleton(handlerClass))
							.createComponentHandler(ComponentParameter.get(rRequest, componentBean));
				} else {
					hdl = (IComponentHandler) ObjectFactory.create(handlerClass);
				}
				rRequest.setRequestAttr(stringClass, hdl);
			}
		}
		return hdl;
	}

	/*--------------------------------- utils -----------------------------------*/

	public static String getLoadingContent() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div style='padding: 10px; font-size: 8.5pt; font-weight: bolder;'>");
		sb.append($m("ComponentUtils.loadingContent.0"));
		sb.append("</div>");
		return sb.toString();
	}
}
