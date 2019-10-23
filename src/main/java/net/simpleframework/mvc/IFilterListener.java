package net.simpleframework.mvc;

import java.io.IOException;
import java.util.EventListener;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IFilterListener extends EventListener, IMVCSettingsAware {

	/**
	 * 过滤器
	 * 
	 * @param rRequest
	 * @param pageDocument
	 * @return
	 * @throws IOException
	 */
	EFilterResult doFilter(PageRequestResponse rRequest, PageDocument pageDocument)
			throws IOException;

	public static enum EFilterResult {
		/**
		 * 
		 */
		SUCCESS,

		/**
		 * 
		 */
		BREAK
	}
}