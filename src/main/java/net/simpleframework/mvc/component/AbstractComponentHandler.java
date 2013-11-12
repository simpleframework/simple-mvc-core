package net.simpleframework.mvc.component;

import java.util.Map;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.common.bean.BeanDefaults;
import net.simpleframework.mvc.AbstractMVCHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentHandler extends AbstractMVCHandler implements
		IComponentHandler {

	@Override
	public void onRender(final ComponentParameter cp) {
	}

	@Override
	public Object getBeanProperty(final ComponentParameter cp, final String beanProperty) {
		final AbstractComponentBean componentBean = cp.componentBean;
		Object val = BeanUtils.getProperty(componentBean, beanProperty);
		if (val == null) {
			val = BeanDefaults.get(componentBean.getClass(), beanProperty);
		}
		return val;
	}

	@Override
	public Map<String, Object> toJSON(final ComponentParameter cp) {
		return new KVMap();
	}

	@Override
	public Map<String, Object> getFormParameters(final ComponentParameter cp) {
		return new KVMap();
	}
}
