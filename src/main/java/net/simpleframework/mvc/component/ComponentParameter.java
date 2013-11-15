package net.simpleframework.mvc.component;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.common.bean.BeanDefaults;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public final class ComponentParameter extends PageParameter {
	private PageDocument documentRef;

	public AbstractComponentBean componentBean;

	public ComponentParameter(final HttpServletRequest request, final HttpServletResponse response,
			final AbstractComponentBean componentBean) {
		super(request, response, null);
		this.componentBean = componentBean;
	}

	@Override
	public PageDocument getPageDocument() {
		PageDocument pageDocument = super.getPageDocument();
		if (pageDocument == null) {
			pageDocument = componentBean.getPageDocument();
		}
		return pageDocument;
	}

	private static final String FORM_PARAMETERS = "@form_parameters";

	@SuppressWarnings("unchecked")
	public Map<String, Object> getFormParameters() {
		Map<String, Object> formParameters = (Map<String, Object>) getRequestAttr(FORM_PARAMETERS);
		if (formParameters == null) {
			setRequestAttr(FORM_PARAMETERS, formParameters = new KVMap());
		}
		return formParameters;
	}

	public ComponentParameter addFormParameter(final String key, final Object val) {
		getFormParameters().put(key, val);
		return this;
	}

	public PageDocument getDocumentRef() {
		return documentRef;
	}

	public IComponentHandler getComponentHandler() {
		return componentBean.getComponentHandler(this);
	}

	@Override
	public String hashId() {
		return componentBean.hashId();
	}

	public String getComponentName() {
		return (String) getBeanProperty("name");
	}

	@Override
	public Object getBeanProperty(final String beanProperty) {
		final IComponentHandler handle = getComponentHandler();
		if (handle != null) {
			return handle.getBeanProperty(this, beanProperty);
		} else {
			Object val = BeanUtils.getProperty(componentBean, beanProperty);
			if (val == null) {
				val = BeanDefaults.get(componentBean.getClass(), beanProperty);
			}
			return val;
		}
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response, final String beanId) {
		return get(new PageRequestResponse(request, response), beanId);
	}

	public static ComponentParameter get(final PageRequestResponse rRequest, final String beanId) {
		// rRequest.request.getSession().getServletContext().
		return get(rRequest,
				ComponentUtils.getComponentBeanByHashId(rRequest, rRequest.getParameter(beanId)));
	}

	public static ComponentParameter get(final PageRequestResponse rRequest,
			final AbstractComponentBean componentBean) {
		final ComponentParameter cp = get(rRequest.request, rRequest.response, componentBean);
		if (rRequest instanceof ComponentParameter) {
			cp.documentRef = ((ComponentParameter) rRequest).documentRef;
		} else if (rRequest instanceof PageParameter) {
			cp.documentRef = ((PageParameter) rRequest).getPageDocument();
		}
		return cp;
	}

	public static ComponentParameter get(final HttpServletRequest request,
			final HttpServletResponse response, final AbstractComponentBean componentBean) {
		return new ComponentParameter(request, response, componentBean);
	}

	public static ComponentParameter getByAttri(final ComponentParameter cp, final String attri) {
		return get(cp, (AbstractComponentBean) cp.componentBean.getAttr(attri));
	}
}
