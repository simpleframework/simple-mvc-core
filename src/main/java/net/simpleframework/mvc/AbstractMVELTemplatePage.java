package net.simpleframework.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.simpleframework.common.th.ParserException;
import net.simpleframework.common.th.ThrowableUtils;
import net.simpleframework.ctx.script.MVEL2Template;
import net.simpleframework.ctx.script.MVEL2Template.INamedTemplate;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractMVELTemplatePage extends AbstractMVCPage {
	@Override
	protected String replaceExpr(final PageParameter pp, final InputStream htmlStream,
			final Map<String, Object> variables) throws IOException {
		try {
			return MVEL2Template.replace(variables, super.replaceExpr(pp, htmlStream, variables),
					new INamedTemplate() {
						@Override
						public Set<String> keySet() {
							return templates.keySet();
						}

						@Override
						public String get(final String key) {
							return getMVELNamedTemplate(pp, key);
						}
					});
		} catch (final Exception e) {
			getLog().error(ThrowableUtils.convertThrowable(e.getCause()));
			throw ParserException.of(getClass().getName() + ": " + e.getMessage());
		}
	}

	public void addMVELNamedTemplate(final PageParameter pp, final String key,
			final Class<? extends AbstractMVCPage> pageClass) {
		if (pageClass != null) {
			templates.put(key, pageClass);
		}
	}

	public String getMVELNamedTemplate(final PageParameter pp, final String key) {
		final Class<? extends AbstractMVCPage> pageClass = templates.get(key);
		if (pageClass != null) {
			String cc = _templates.get(pageClass);
			if (cc == null) {
				_templates.put(pageClass, cc = pp.includeUrl(pageClass));
			}
			return cc;
		}
		return null;
	}

	private final Map<String, Class<? extends AbstractMVCPage>> templates = new HashMap<>();

	private final Map<Class<? extends AbstractMVCPage>, String> _templates = new HashMap<>();
}
