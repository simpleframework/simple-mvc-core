package net.simpleframework.mvc;

import java.io.IOException;
import java.util.EventListener;

import javax.servlet.FilterChain;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IFilterListener extends EventListener, IMVCConst {

	/**
	 * 过滤器
	 * 
	 * @param rRequest
	 * @param filterChain
	 * @return
	 * @throws IOException
	 */
	EFilterResult doFilter(PageRequestResponse rRequest, FilterChain filterChain) throws IOException;

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