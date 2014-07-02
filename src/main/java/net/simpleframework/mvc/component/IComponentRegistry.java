package net.simpleframework.mvc.component;

import net.simpleframework.mvc.IPageResourceProvider;
import net.simpleframework.mvc.PageParameter;

/**
 * 提供给开发者开发自定义组件组件接口
 * 
 * 
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IComponentRegistry {
	/**
	 * 组件的名称，这个值就是在XML描述中声明的组件标签，且必须唯一
	 */
	String getComponentName();

	/**
	 * 获取组件的渲染器实例
	 */
	IComponentRender getComponentRender();

	/**
	 * 获取组件的资源提供者实例
	 */
	IComponentResourceProvider getComponentResourceProvider();

	/**
	 * 创建组件的元信息定义实例,组件的元信息来自XML描述文件， 该实例将按XML中的定义来初始化Bean的属性
	 * 
	 * @param pParameter
	 * @param data
	 *        根据xml元素或map对象对象创建
	 * @return
	 */
	AbstractComponentBean createComponentBean(PageParameter pp, Object attriData);

	/**
	 * 获得页面资源
	 */
	IPageResourceProvider getPageResourceProvider();
}
