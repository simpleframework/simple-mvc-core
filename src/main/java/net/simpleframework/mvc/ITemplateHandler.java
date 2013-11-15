package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface ITemplateHandler {

	/**
	 * 获取模板头部页面
	 * 
	 * @return
	 */
	Class<? extends AbstractMVCPage> getHeaderPage();

	/**
	 * 获取模板尾部页面
	 * 
	 * @return
	 */
	Class<? extends AbstractMVCPage> getFooterPage();

	String getFavicon(PageParameter pp);

	/**
	 * 是否显示菜单栏
	 * 
	 * @param pp
	 * @return
	 */
	boolean isShowMenubar(PageParameter pp);
}
