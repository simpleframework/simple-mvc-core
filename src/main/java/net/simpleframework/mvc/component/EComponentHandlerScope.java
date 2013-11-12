package net.simpleframework.mvc.component;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public enum EComponentHandlerScope {

	/**
	 * handle为单实例
	 */
	singleton,

	/**
	 * handle将会每次请求创建
	 */
	prototype
}
