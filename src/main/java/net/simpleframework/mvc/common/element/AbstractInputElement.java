package net.simpleframework.mvc.common.element;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.html.HtmlEncoder;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings("unchecked")
public abstract class AbstractInputElement<T extends AbstractInputElement<T>> extends
		AbstractElement<T> {
	private boolean readonly;

	private EInputType inputType;

	/* 是否选中，checkbox和radio */
	private boolean checked;

	/* 仅对textarea */
	private int rows = 4;
	private boolean autoRows;

	/* change事件 */
	private String onchange;

	public boolean isReadonly() {
		return readonly;
	}

	public T setReadonly(final boolean readonly) {
		this.readonly = readonly;
		return (T) this;
	}

	public EInputType getInputType() {
		return inputType == null ? EInputType.text : inputType;
	}

	public T setInputType(final EInputType inputType) {
		this.inputType = inputType;
		return (T) this;
	}

	public boolean isChecked() {
		return checked;
	}

	public T setChecked(final boolean checked) {
		this.checked = checked;
		return (T) this;
	}

	public int getRows() {
		if (rows < 1) {
			return 1;
		}
		return rows;
	}

	public T setRows(final int rows) {
		this.rows = rows;
		return (T) this;
	}

	public boolean isAutoRows() {
		return autoRows;
	}

	public T setAutoRows(final boolean autoRows) {
		this.autoRows = autoRows;
		return (T) this;
	}

	public String getOnchange() {
		return onchange;
	}

	public T setOnchange(final String onchange) {
		this.onchange = onchange;
		return (T) this;
	}

	public T setValue(final String value) {
		return setText(value);
	}

	public T setValue(final PageRequestResponse rRequest) {
		return setText(rRequest.getParameter(getName()));
	}

	@Override
	protected void doAttri(final StringBuilder sb) {
		if (!isDisabled()) {
			addAttribute("onchange", getOnchange());
		} else {
			sb.append(" disabled");
		}

		final EInputType type = getInputType();
		if (isReadonly()) {
			sb.append(" readonly");
		}
		if (type == EInputType.checkbox || type == EInputType.radio) {
			if (isChecked()) {
				sb.append(" checked");
			}
		}
		if (type == EInputType.textarea) {
			if (isAutoRows()) {
				addStyle("line-height: 21px; min-height: " + (21 * getRows()) + "px;");
				final int rows = StringUtils.charCount(getText(), '\n') + 1;
				addAttribute("rows", rows);
				addStyle("height: " + (21 * rows) + "px");

				addAttribute("oninput",
						"this.style.height='0px'; this.style.height = (this.scrollHeight + 'px');");
				addAttribute("onpropertychange", "this.style.height = (this.scrollHeight + 'px');");
			} else {
				addAttribute("rows", getRows());
			}
		}
		super.doAttri(sb);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		final EInputType type = getInputType();
		if (type == EInputType.select || type == EInputType.textarea) {
			sb.append("<").append(type);
		} else {
			sb.append("<input");
			if (type == EInputType.textButton || type == EInputType.multiSelect) {
				sb.append(" type='text'");
			} else {
				sb.append(" type='").append(type).append("'");
			}
		}
		doAttri(sb);
		if (type == EInputType.select || type == EInputType.textarea) {
			sb.append(">");
			final String txt = getText();
			if (StringUtils.hasText(txt)) {
				if (type == EInputType.select) {
					sb.append(txt);
				} else {
					sb.append(HtmlEncoder.text(txt));
				}
			}
			sb.append("</").append(type).append(">");
		} else {
			final String txt = getText();
			if (StringUtils.hasText(txt)) {
				sb.append(" value=\"").append(HtmlEncoder.text(txt)).append("\"");
			} else {
				if (type == EInputType.checkbox || type == EInputType.radio) {
					sb.append(" value=\"true\"");
				}
			}
			sb.append(" />");
		}
		return sb.toString();
	}
}
