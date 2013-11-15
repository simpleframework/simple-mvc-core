package net.simpleframework.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.simpleframework.common.IoUtils;
import net.simpleframework.common.coll.AbstractKVMap;
import net.simpleframework.ctx.script.MVEL2Template;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
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
		final NamedTemplate nt = createNamedTemplates(pp);
		pp.setRequestAttr("NamedTemplate", nt);
		return super.forward(pp);
	}

	@Override
	protected String replaceExpr(final PageParameter pp, final InputStream htmlStream,
			final Map<String, Object> variables) throws IOException {
		final NamedTemplate nt = (NamedTemplate) pp.getRequestAttr("NamedTemplate");
		return MVEL2Template.replace(variables,
				IoUtils.getStringFromInputStream(htmlStream, getChartset()), nt == null ? null : nt);
	}

	public static class NamedTemplate extends AbstractKVMap<String, NamedTemplate> {
		private final PageParameter pp;

		public NamedTemplate(final PageParameter pp) {
			this.pp = pp;
		}

		public NamedTemplate add(final String key, final Class<? extends AbstractMVCPage> pageClass) {
			// + "?referer=" + pp.getRequestURI()
			return add(key, pp.includeUrl(pageClass));
		}

		private static final long serialVersionUID = 3291319165215636903L;
	}
}
