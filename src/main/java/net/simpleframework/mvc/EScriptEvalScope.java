package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public enum EScriptEvalScope {
	/**
	 * 不执行脚本
	 */
	none,

	/**
	 * 第一次运行的时候执行
	 */
	application,

	/**
	 * 每次运行的时候都执行
	 */
	request
}
