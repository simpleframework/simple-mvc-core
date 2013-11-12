package net.simpleframework.mvc;

import java.util.Map;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.coll.ArrayUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class DefaultPageHandler extends AbstractMVCHandler implements IPageHandler {

	@Override
	public void onPageLoad(final PageParameter pp, final Map<String, Object> dataBinding,
			final PageSelector selector) throws Exception {
	}

	@Override
	public Object getBeanProperty(final PageParameter pp, final String beanProperty) {
		return BeanUtils.getProperty(pp.getPageBean(), beanProperty);
	}

	protected String[] addImportPage(final PageParameter pp, final String[] importPage) {
		final PageDocument pageDocument = pp.getPageDocument();
		final String[] importPage2;
		if (pageDocument == null
				|| (importPage2 = pageDocument.getPageBean().getImportPage()) == null
				|| importPage2.length == 0) {
			return importPage;
		}
		return ArrayUtils.add(importPage2, importPage);
	}

	@Override
	public void onBeforeComponentRender(final PageParameter pp) {
	}
}
