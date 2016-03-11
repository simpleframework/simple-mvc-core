package net.simpleframework.mvc.common;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IDownloadHandler {

	/**
	 * 文件下载后触发
	 * 
	 * @param beanId
	 * @param length
	 * @param filetype
	 * @param topic
	 */
	void onDownloaded(Object beanId, long length, String filetype, String topic);
}
