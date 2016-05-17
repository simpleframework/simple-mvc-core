package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class SpanElement extends AbstractTagElement<SpanElement> {

	public static SpanElement ELLIPSIS = new SpanElement("&hellip;").addStyle("color: black;");

	public static SpanElement SEP(final String text, final int mwidth) {
		return new SpanElement(text).setStyle("margin: 0px " + mwidth + "px;");
	}

	public static SpanElement SEP(final int mwidth) {
		return SEP("|", mwidth);
	}

	public static SpanElement SEP() {
		return SEP(5);
	}

	public static SpanElement NAV(final String text, final int mwidth) {
		return new SpanElement(text).addStyle("margin: 0px " + mwidth + "px;");
	}

	public static SpanElement NAV(final int mwidth) {
		return NAV("&raquo;", mwidth);
	}

	public static SpanElement NAV() {
		return NAV(2);
	}

	public static SpanElement SPACE(final int width) {
		return new SpanElement().addStyle("width: " + width + "px; display: inline-block;");
	}

	public static SpanElement SPACE = SPACE(5);

	public static SpanElement SPACE10 = SPACE(10);

	public static SpanElement SPACE15 = SPACE(15);

	public static SpanElement shortText(final Object text) {
		return new SpanElement(text).addStyle("margin-left: 3px; font-size: 9px; color: #666;");
	}

	public static SpanElement strongText(final Object text) {
		return new SpanElement(text).addStyle("font-size: 9.5pt; color: #666; font-weight: bold;");
	}

	public static SpanElement warnText(final Object text) {
		return new SpanElement(text).addStyle("font-size: 11.5pt; color: #e00;");
	}

	public static SpanElement num(final Object num) {
		return new SpanElement(num).setClassName("num");
	}

	public static SpanElement color777(final Object txt) {
		return color(txt, "#777");
	}

	public static SpanElement color999(final Object txt) {
		return color(txt, "#999");
	}

	public static SpanElement color555(final Object txt) {
		return color(txt, "#555");
	}

	public static SpanElement color333(final Object txt) {
		return color(txt, "#333");
	}

	public static SpanElement color060(final Object txt) {
		return color(txt, "#060");
	}

	public static SpanElement color600(final Object txt) {
		return color(txt, "#600");
	}

	public static SpanElement colora00(final Object txt) {
		return color(txt, "#a00");
	}

	public static SpanElement colorf00(final Object txt) {
		return color(txt, "#f00");
	}

	public static SpanElement color(final Object txt, final String color) {
		return new SpanElement(txt).setColor(color);
	}

	public SpanElement() {
	}

	public SpanElement(final Object text) {
		super(text);
	}

	public SpanElement(final Object text, final String className) {
		super(text);
		setClassName(className);
	}

	@Override
	protected String tag() {
		return "span";
	}
}
