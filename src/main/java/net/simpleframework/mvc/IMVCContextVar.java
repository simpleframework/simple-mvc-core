package net.simpleframework.mvc;

import javax.servlet.ServletContext;

import net.simpleframework.ctx.ApplicationContextFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IMVCContextVar {

	static IMVCContext mvcContext = (IMVCContext) ApplicationContextFactory.ctx();

	static MVCSettings settings = mvcContext != null ? mvcContext.getMVCSettings() : null;

	static ServletContext servlet = mvcContext != null ? mvcContext.getServletContext() : null;
}
