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

	/**
	 * 创建mvel预定义好的模板
	 * 
	 * html写法：$includeNamed{'key'}
	 * 
	 * @return
	 */
	protected NamedTemplate createNamedTemplates(final PageParameter pp) {
		return null;
	}

	@Override
	public IForward forward(final PageParameter pp) {
		getNamedTemplate(pp);
		return super.forward(pp);
	}

	protected NamedTemplate getNamedTemplate(final PageParameter pp) {
		NamedTemplate nt = (NamedTemplate) pp.getRequestAttr("NamedTemplate");
		if (nt == null) {
			pp.setRequestAttr("NamedTemplate", nt = createNamedTemplates(pp));
		}
		return nt;
	}

	@Override
	protected String replaceExpr(final PageParameter pp, final InputStream htmlStream,
			final Map<String, Object> variables) throws IOException {
		final NamedTemplate nt = getNamedTemplate(pp);
		return MVEL2Template.replace(variables, super.replaceExpr(pp, htmlStream, variables),
				nt == null ? null : nt);
	}

	public static class NamedTemplate implements INamedTemplate {
		private final Map<String, TemplateVal> templates = new HashMap<String, TemplateVal>();

		private final PageParameter pp;

		public NamedTemplate(final PageParameter pp) {
			this.pp = pp;
		}

		@Override
		public String get(final String key) {
			final TemplateVal val = templates.get(key);
			return val != null ? val.getVal(pp) : null;
		}

		@Override
		public Set<String> keySet() {
			return templates.keySet();
		}

		public NamedTemplate add(final String key, final Class<? extends AbstractMVCPage> pageClass) {
			// + "?referer=" + pp.getRequestURI()
			templates.put(key, new TemplateVal(pageClass));
			return this;
		}
	}

	static class TemplateVal {
		Class<? extends AbstractMVCPage> pageClass;

		String val;

		TemplateVal(final Class<? extends AbstractMVCPage> pageClass) {
			this.pageClass = pageClass;
		}

		String getVal(final PageParameter pp) {
			if (val == null) {
				val = pp.includeUrl(pageClass);
			}
			return val;
		}
	}
}
