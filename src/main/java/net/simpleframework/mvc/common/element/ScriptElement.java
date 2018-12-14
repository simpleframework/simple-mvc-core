package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ScriptElement extends AbstractTagElement<SpanElement> {

	@Override
	protected String tag() {
		return "script";
	}

	@Override
	public String toString() {
		addAttribute("type", "text/javascript");
		return super.toString();
	}

	private static final long serialVersionUID = -2792747826817597109L;
}
