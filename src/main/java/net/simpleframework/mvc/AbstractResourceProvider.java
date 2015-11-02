package net.simpleframework.mvc;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.mvc.common.DeployUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractResourceProvider extends ObjectEx implements IResourceProvider,
		IMVCConst {

	@Override
	public String getResourceHomePath() {
		return getResourceHomePath(getClass());
	}

	@Override
	public String getResourceHomePath(final Class<?> resourceClass) {
		return DeployUtils.getResourcePath(resourceClass);
	}

	@Override
	public String getCssResourceHomePath(final PageParameter pp) {
		return getCssResourceHomePath(pp, getClass());
	}

	@Override
	public String getCssResourceHomePath(final PageParameter pp, final Class<?> resourceClass) {
		return getResourceHomePath(resourceClass) + "/css/" + getSkin(pp);
	}

	@Override
	public String[] getJavascriptPath(final PageParameter pp) {
		return null;
	}

	@Override
	public String[] getCssPath(final PageParameter pp) {
		return null;
	}

	@Override
	public String[] getJarPath() {
		return null;
	}

	@Override
	public String getSkin(final PageParameter pp) {
		String skin = getSkin();
		if (StringUtils.hasText(skin)) {
			return skin;
		} else {
			skin = (String) pp.getSessionAttr(SESSION_ATTRI_SKIN);
			if (StringUtils.hasText(skin)) {
				return skin;
			} else {
				return null;
			}
		}
	}

	private String skin;

	@Override
	public String getSkin() {
		return skin;
	}

	@Override
	public void setSkin(final String skin) {
		this.skin = skin;
	}
}
