package net.simpleframework.mvc.component;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IComponentRender {

	/**
	 * 获取组件的注册器对象
	 * 
	 * @return
	 */
	IComponentRegistry getComponentRegistry();
}
