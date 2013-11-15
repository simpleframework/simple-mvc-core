package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public enum EInputType {
	text,

	/**
	 * 按钮选择
	 */
	textButton,

	hidden,

	checkbox,

	radio,

	password,

	/**
	 * 文件
	 */
	file,

	textarea,

	select,

	/**
	 * 带按钮的多选
	 */
	multiSelect,

	/**
	 * html5特有的类型
	 */
	range,

	search,

	number,

	tel,

	email
}
