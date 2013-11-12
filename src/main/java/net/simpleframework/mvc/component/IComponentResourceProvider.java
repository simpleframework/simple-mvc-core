package net.simpleframework.mvc.component;

import net.simpleframework.common.StringUtils;
import net.simpleframework.mvc.AbstractResourceProvider;
import net.simpleframework.mvc.IPageResourceProvider;
import net.simpleframework.mvc.IResourceProvider;
import net.simpleframework.mvc.PageParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public interface IComponentResourceProvider extends IResourceProvider {

	/**
	 * 获取依赖的组件
	 * 
	 * @param pParameter
	 * @return
	 */
	String[] getDependentComponents(PageParameter pp);

	/**
	 * 组件的注册器
	 * 
	 * @return
	 */
	IComponentRegistry getComponentRegistry();

	/**
	 * 获取页面的资源提供者
	 * 
	 * @return
	 */
	IPageResourceProvider getPageResourceProvider();

	/**
	 * 抽象实现
	 */
	public static abstract class AbstractComponentResourceProvider extends AbstractResourceProvider
			implements IComponentResourceProvider {
		private final IComponentRegistry componentRegistry;

		public AbstractComponentResourceProvider(final IComponentRegistry componentRegistry) {
			this.componentRegistry = componentRegistry;
		}

		@Override
		public String[] getDependentComponents(final PageParameter pp) {
			return null;
		}

		@Override
		public IComponentRegistry getComponentRegistry() {
			return componentRegistry;
		}

		@Override
		public IPageResourceProvider getPageResourceProvider() {
			return getComponentRegistry().getPageResourceProvider();
		}

		@Override
		public String getSkin(final PageParameter pp) {
			final String skin = super.getSkin(pp);
			if (StringUtils.hasText(skin)) {
				return skin;
			}
			return getComponentRegistry().getPageResourceProvider().getSkin(pp);
		}
	}
}
