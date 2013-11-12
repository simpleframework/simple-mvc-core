package net.simpleframework.mvc;

import javax.servlet.ServletContext;

import net.simpleframework.ctx.ApplicationContextFactory;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IMVCContextVar {

	static IMVCContext ctx = (IMVCContext) ApplicationContextFactory.ctx();

	static MVCSettings settings = ctx != null ? ctx.getMVCSettings() : MVCSettings.get();

	static ServletContext servlet = ctx != null ? ctx.getServletContext() : null;
}
