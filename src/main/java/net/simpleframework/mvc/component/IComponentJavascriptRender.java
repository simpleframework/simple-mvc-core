package net.simpleframework.mvc.component;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IComponentJavascriptRender extends IComponentRender {

	/**
	 * 组件渲染需要生成的js代码
	 * 
	 * @param cParameter
	 * @return
	 */
	String getJavascriptCode(ComponentParameter cp);
}
