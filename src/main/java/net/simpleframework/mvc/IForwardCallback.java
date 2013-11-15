package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IForwardCallback<T extends IForward> {

	/**
	 * IForward的回调接口
	 * 
	 * @param t
	 */
	void doAction(T t);

	public static interface IJsonForwardCallback extends IForwardCallback<JsonForward> {
	}

	public static interface IJavascriptForwardCallback extends IForwardCallback<JavascriptForward> {
	}
}
