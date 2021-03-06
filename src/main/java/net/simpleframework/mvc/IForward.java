package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IForward {

	String getResponseText(PageRequestResponse rRequest);

	/**
	 * 是否经过html解析
	 * 
	 * @return
	 */
	boolean isHtmlParser();
}
