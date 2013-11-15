package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class SpanElement extends AbstractTagElement<SpanElement> {
	public static SpanElement SEP = new SpanElement("|").setStyle("margin: 0px 5px;");

	public static SpanElement NAV = new SpanElement("&raquo;").setStyle("margin: 0px 2px;");

	public static SpanElement ELLIPSIS = new SpanElement("&hellip;").setStyle("color: black;");

	public static SpanElement SPACE(final int width) {
		return new SpanElement().setStyle("width: " + width + "px; display: inline-block;");
	}

	public static SpanElement SPACE = SPACE(6);

	public static SpanElement SPACE15 = SPACE(15);

	public static SpanElement shortText(final Object text) {
		return new SpanElement(text).setStyle("margin-left: 3px; font-size: 9px; color: #666;");
	}

	public static SpanElement strongText(final Object text) {
		return new SpanElement(text).setStyle("font-size: 10.5pt; color: #a00; font-weight: bold;");
	}

	public static SpanElement num(final Object num) {
		return new SpanElement(num).setClassName("num");
	}

	public SpanElement() {
	}

	public SpanElement(final Object text) {
		super(text);
	}

	@Override
	protected String tag() {
		return "span";
	}
}
