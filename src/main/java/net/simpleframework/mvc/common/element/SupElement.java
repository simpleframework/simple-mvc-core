package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class SupElement extends AbstractTagElement<SupElement> {

	public static SupElement num(final int num) {
		return new SupElement(num).setStyle("margin-left: 2px;");
	}

	private boolean highlight;

	public SupElement() {
	}

	public SupElement(final Object text) {
		super(text);
	}

	public boolean isHighlight() {
		return highlight;
	}

	public SupElement setHighlight(final boolean highlight) {
		this.highlight = highlight;
		return this;
	}

	@Override
	protected String tag() {
		return "sup";
	}

	@Override
	protected void doAttri(final StringBuilder sb) {
		if (isHighlight()) {
			addClassName("highlight");
		}
		super.doAttri(sb);
	}
}
