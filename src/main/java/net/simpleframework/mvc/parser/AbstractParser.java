package net.simpleframework.mvc.parser;

import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.mvc.IMVCSettingsAware;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractParser extends ObjectEx implements IMVCSettingsAware {

	protected void doBeforeRender(final ComponentParameter cp) {
		final IComponentHandler handle = cp.getComponentHandler();
		if (handle != null) {
			handle.onRender(cp);
		}
	}
}
