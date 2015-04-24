package net.simpleframework.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.simpleframework.ctx.script.MVEL2Template;
import net.simpleframework.ctx.script.MVEL2Template.INamedTemplate;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractMVELTemplatePage extends AbstractMVCPage {
	@Override
	protected String replaceExpr(final PageParameter pp, final InputStream htmlStream,
			final Map<String, Object> variables) throws IOException {
		return MVEL2Template.replace(variables, super.replaceExpr(pp, htmlStream, variables),
				new INamedTemplate() {
					@Override
					public Set<String> keySet() {
						return templates.keySet();
					}

					@Override
					public String get(final String key) {
						return getMVELNamedTemplate(key);
					}
				});
	}

	public void addMVELNamedTemplate(final PageParameter pp, final String key,
			final Class<? extends AbstractMVCPage> pageClass) {
		if (pageClass != null) {
			templates.put(key, pp.includeUrl(pageClass));
		}
	}

	public String getMVELNamedTemplate(final String key) {
		return templates.get(key);
	}

	private final Map<String, String> templates = new HashMap<String, String>();
}
