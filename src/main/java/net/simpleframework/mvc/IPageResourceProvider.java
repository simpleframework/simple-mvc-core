package net.simpleframework.mvc;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.common.DeployWeb;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IPageResourceProvider extends IResourceProvider, IMVCSettingsAware {

	/**
	 * 页面资源提供者的唯一名称
	 * 
	 * @return
	 */
	String getName();

	public static class MVCPageResourceProvider extends AbstractResourceProvider
			implements IPageResourceProvider {

		@Override
		public String getName() {
			return DEFAULT_SKIN;
		}

		@Override
		public String[] getCssPath(final PageParameter pp) {
			final String rPath = getCssResourceHomePath(pp, IPageResourceProvider.class);
			return new String[] { rPath + "/core.css" };
		}

		@Override
		public String[] getJavascriptPath(final PageParameter pp) {
			return new String[] {
					DeployWeb.getResourcePath(IPageResourceProvider.class) + "/js/core.js" };
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
