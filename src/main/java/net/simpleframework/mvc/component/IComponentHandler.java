package net.simpleframework.mvc.component;

import java.util.Map;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IComponentHandler {

	/**
	 * 当Handler被创建时触发
	 * 
	 * @param cParameter
	 */
	// void onCreated(ComponentParameter cp);

	/**
	 * 当组件被渲染时触发
	 * 
	 * @param cParameter
	 */
	void onRender(ComponentParameter cp);

	/**
	 * 通过该函数动态装载属性，在组件开发中需要通过调用该函数，否则使用者覆盖此类没有效果
	 * 
	 * @param compParameter
	 * @param beanProperty
	 * @return
	 */
	Object getBeanProperty(ComponentParameter cp, String beanProperty);

	/**
	 * 
	 * @param cParameter
	 * @return
	 */
	Map<String, Object> toJSON(ComponentParameter cp);

	/**
	 * 
	 * @param cParameter
	 * @return
	 */
	Map<String, Object> getFormParameters(ComponentParameter cp);
}