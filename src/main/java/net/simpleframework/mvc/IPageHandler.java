package net.simpleframework.mvc;

import java.util.Map;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IPageHandler {

	/**
	 * @param pParameter
	 * @param beanProperty
	 * @return
	 */
	Object getBeanProperty(PageParameter pp, String beanProperty);

	/**
	 * 
	 * @param pParameter
	 * @param dataBinding
	 * @param selector
	 */
	void onPageLoad(PageParameter pp, Map<String, Object> dataBinding, PageSelector selector)
			throws Exception;

	/**
	 * 
	 * @param pParameter
	 */
	void onBeforeComponentRender(PageParameter pp);

	public static class PageSelector {
		public String visibleToggleSelector;

		public String readonlySelector;

		public String disabledSelector;
	}
}
