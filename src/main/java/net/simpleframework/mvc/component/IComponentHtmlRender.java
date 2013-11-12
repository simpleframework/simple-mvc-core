package net.simpleframework.mvc.component;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IComponentHtmlRender extends IComponentRender {

	/**
	 * 生成组件的html代码
	 * 
	 * @param compParameter
	 * @return
	 */
	String getHtml(ComponentParameter cp);

	/**
	 * 生成执行的js代码
	 * 
	 * @param compParameter
	 * @return
	 */
	String getHtmlJavascriptCode(ComponentParameter cp);
}
