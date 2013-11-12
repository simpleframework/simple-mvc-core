package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class ParagraphElement extends AbstractTagElement<ParagraphElement> {
	public ParagraphElement() {
	}

	public ParagraphElement(final String id) {
		setId(id);
	}

	@Override
	protected String tag() {
		return "p";
	}
}
