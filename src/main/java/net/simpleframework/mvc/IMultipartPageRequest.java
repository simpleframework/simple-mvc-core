package net.simpleframework.mvc;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IMultipartPageRequest extends HttpServletRequest {

	IMultipartFile getFile(String name);

	/**
	 * 获取所有的附件名称
	 * 
	 * @return
	 */
	Enumeration<String> getFileNames();
}