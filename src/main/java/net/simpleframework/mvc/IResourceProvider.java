package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IResourceProvider {

	/**
	 * 获取资源的路径
	 * 
	 * @return
	 */
	String getResourceHomePath();

	/**
	 * 
	 * @param resourceClass
	 * @return
	 */
	String getResourceHomePath(Class<?> resourceClass);

	String getCssResourceHomePath(PageParameter pp);

	/**
	 * 获取css的根目录
	 * 
	 * @param rr
	 * @param resourceClass
	 * @return
	 */
	String getCssResourceHomePath(PageParameter pp, Class<?> resourceClass);

	/**
	 * 获取资源javascript的路径
	 * 
	 * @param rr
	 * @return
	 */
	String[] getJavascriptPath(PageParameter pp);

	/**
	 * 获取资源css的路径
	 * 
	 * @param rr
	 * @return
	 */
	String[] getCssPath(PageParameter pp);

	/**
	 * 获取依赖的jar路径
	 * 
	 * @return
	 */
	String[] getJarPath();

	/**
	 * 获取当前的皮肤
	 * 
	 * @param rr
	 * @return
	 */
	String getSkin(PageParameter pp);

	String getSkin();

	void setSkin(String skin);
}
