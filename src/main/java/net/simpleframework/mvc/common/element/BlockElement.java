package net.simpleframework.mvc.common.element;

import net.simpleframework.common.web.html.HtmlUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class BlockElement extends AbstractTagElement<BlockElement> {

	public static BlockElement CLEAR = new BlockElement().setClassName("clearfix");

	public static BlockElement tip(final String text) {
		return new BlockElement().addStyle("display: none;")
				.setText(HtmlUtils.convertHtmlLines(text));
	}

	public static BlockElement tipHTML(final String html) {
		return new BlockElement().addStyle("display: none;").setText(html);
	}

	public static BlockElement nav() {
		return new BlockElement().setClassName("nav_arrow");
	}

	public BlockElement() {
	}

	// public BlockElement(final String id) {
	// setId(id);
	// }

	@Override
	protected String tag() {
		return "div";
	}
}
