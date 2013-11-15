package net.simpleframework.mvc;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IPageResourceProvider extends IResourceProvider {

	/**
	 * 页面资源提供者的唯一名称
	 * 
	 * @return
	 */
	String getName();

	/**
	 * 输出到浏览器的初始化代码
	 * 
	 * @param pageParameter
	 * @return
	 */
	String getInitJavascriptCode(final PageParameter pp);

	public static class MVCPageResourceProvider extends AbstractResourceProvider implements
			IPageResourceProvider {

		@Override
		public String getName() {
			return DEFAULT_SKIN;
		}

		@Override
		public String[] getCssPath(final PageParameter pp) {
			final String rPath = MVCUtils.getCssResourcePath(pp);
			return new String[] { rPath + "/core.css", rPath + "/icon.css" };
		}

		@Override
		public String[] getJavascriptPath(final PageParameter pp) {
			return new String[] { MVCUtils.getPageResourcePath() + "/js/core.js" };
		}

		@Override
		public String getInitJavascriptCode(final PageParameter pp) {
			final StringBuilder sb = new StringBuilder();
			final AbstractMVCPage pageView;
			final String jsCode;
			if ((pageView = pp.getPage()) != null
					&& StringUtils.hasText(jsCode = pageView.getInitJavascriptCode(pp))) {
				sb.append(jsCode);
			}
			return sb.toString();
		}

		@Override
		public boolean equals(final Object obj) {
			return getName().equalsIgnoreCase(Convert.toString(obj));
		}

		@Override
		public String toString() {
			final String name = getName();
			return name != null ? name : super.toString();
		}

		@Override
		public String getSkin(final PageParameter pp) {
			final String skin = super.getSkin(pp);
			return StringUtils.text(skin, DEFAULT_SKIN);
		}

		public final static String DEFAULT_SKIN = "default";
	}
}
