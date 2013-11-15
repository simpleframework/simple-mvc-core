package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class TagElement extends AbstractTagElement<TagElement> {
	private final String _tag;

	public TagElement(final String tag) {
		_tag = tag;
	}

	@Override
	protected String tag() {
		return _tag;
	}

	public static TagElement table() {
		return new TagElement("table").setWidth("100%");
	}

	public static TagElement tr() {
		return new TagElement("tr");
	}

	public static TagElement td() {
		return new TagElement("td");
	}

	public static TagElement td(final ElementList elements) {
		return td().addElements(elements);
	}
}
