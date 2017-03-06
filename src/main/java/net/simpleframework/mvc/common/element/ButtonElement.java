package net.simpleframework.mvc.common.element;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.common.StringUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ButtonElement extends AbstractElement<ButtonElement> {

	public static final ButtonElement closeBtn() {
		return new ButtonElement().setOnclick("$win(this).close();").setText($m("Button.Close"));
	}

	public static final ButtonElement editBtn() {
		return new ButtonElement($m("Edit"));
	}

	public static final ButtonElement addBtn() {
		return new ButtonElement($m("Add"));
	}

	public static final ButtonElement deleteBtn() {
		return new ButtonElement($m("Delete"));
	}

	public static final ButtonElement saveBtn() {
		return new ButtonElement().setHighlight(true).setText($m("Button.Save"));
	}

	public static final ButtonElement okBtn() {
		return new ButtonElement().setHighlight(true).setText($m("Button.Ok"));
	}

	public static final ButtonElement logBtn() {
		return new ButtonElement($m("Button.Log"));
	}

	public static final ButtonElement cancelBtn() {
		return new ButtonElement($m("Button.Cancel"));
	}

	public static final ButtonElement viewBtn() {
		return new ButtonElement($m("Button.View"));
	}

	private boolean highlight;

	public ButtonElement() {
	}

	public ButtonElement(final Object text) {
		setText(text);
	}

	public boolean isHighlight() {
		return highlight;
	}

	public ButtonElement setHighlight(final boolean highlight) {
		this.highlight = highlight;
		return this;
	}

	@Override
	public String toString() {
		if (isHighlight()) {
			addClassName("button2");
		}
		if (isDisabled()) {
			addClassName("disabled_color");
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"button\"");
		doAttri(sb);
		final String txt = getText();
		if (StringUtils.hasText(txt)) {
			sb.append(" value=\"").append(txt).append("\"");
		}
		sb.append(" />");
		return sb.toString();
	}
}
