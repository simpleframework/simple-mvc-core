package net.simpleframework.mvc;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.simpleframework.ctx.IApplicationContextBase;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IMVCContext extends IApplicationContextBase {

	ServletContext getServletContext();

	void setServletContext(ServletContext servletContext);

	/**
	 * 创建缺省的页面资源提供者
	 * 
	 * @return
	 */
	IPageResourceProvider getDefaultPageResourceProvider();

	/**
	 * 创建IMultipartPageRequest的实现
	 * 
	 * @param request
	 * @param maxUploadSize
	 * @return
	 * @throws IOException
	 */
	IMultipartPageRequest createMultipartPageRequest(HttpServletRequest request, int maxUploadSize)
			throws IOException;

	/**
	 * 包装HttpSession类的实现
	 * 
	 * @return
	 */
	HttpSession createHttpSession(HttpSession httpSession);

	/**
	 * 是否为系统url
	 * 
	 * @param request
	 * @return
	 */
	boolean isSystemUrl(PageRequestResponse rRequest);

	/**
	 * 获取模板实例。模板开发者需要继承该接口，该接口完成app和模板的连接
	 * 
	 * @param pp
	 * @return
	 */
	ITemplateHandler getTemplate(PageParameter pp);

	/**
	 * 后处理中，组合html的类
	 * 
	 * 通过覆盖，可以自定义自己的html
	 * 
	 * @return
	 */
	MVCHtmlBuilder getPageHtmlBuilder();

	/**
	 * 获取Servlet相关的监听器,参考
	 * 
	 * @return
	 */
	MVCEventAdapter getEventAdapter();

	/**
	 * 获取mvc的配置
	 * 
	 * @return
	 */
	MVCSettings getMVCSettings();

	void setMVCSettings(MVCSettings settings);

	// -------------------------Listeners

	/**
	 * 添加过滤监听器
	 * 
	 * @return
	 */
	void addFilterListener(IFilterListener listener);

	/**
	 * 获取所有的过滤监听器
	 * 
	 * @return
	 */
	Collection<IFilterListener> getFilterListeners();
}
