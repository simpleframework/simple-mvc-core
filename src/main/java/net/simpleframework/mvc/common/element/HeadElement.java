package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class HeadElement extends AbstractTagElement<HeadElement> {

	public static HeadElement h1(final Object text) {
		return new HeadElement().setSize(1).setText(text);
	}

	public static HeadElement h2(final Object text) {
		return new HeadElement().setSize(2).setText(text);
	}

	public static HeadElement h3(final Object text) {
		return new HeadElement().setSize(3).setText(text);
	}

	public static HeadElement h4(final Object text) {
		return new HeadElement().setSize(4).setText(text);
	}

	public static HeadElement h5(final Object text) {
		return new HeadElement().setSize(5).setText(text);
	}

	public static HeadElement h6(final Object text) {
		return new HeadElement().setSize(6).setText(text);
	}

	private int size;

	private HeadElement() {
	}

	private HeadElement setSize(final int size) {
		this.size = size;
		return this;
	}

	@Override
	protected String tag() {
		return "H" + size;
	}

	private static final long serialVersionUID = -6640688701837620292L;
}
