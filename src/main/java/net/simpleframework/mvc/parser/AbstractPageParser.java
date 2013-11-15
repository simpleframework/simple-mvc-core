package net.simpleframework.mvc.parser;

import net.simpleframework.mvc.PageParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractPageParser extends AbstractParser implements IPageParser {
	protected PageParameter pp;

	protected AbstractPageParser(final PageParameter pp) {
		this.pp = pp;
	}

	@Override
	public PageParameter getPageParameter() {
		return pp;
	}
}
