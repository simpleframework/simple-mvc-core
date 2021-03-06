package net.simpleframework.mvc.common.element;

import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.html.HtmlEncoder;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings({ "unchecked", "serial" })
public abstract class AbstractInputElement<T extends AbstractInputElement<T>>
		extends AbstractElement<T> {
	private boolean readonly;

	private EInputType inputType;

	/* 是否选中，checkbox和radio */
	private boolean checked;

	/* 仅对textarea */
	private int rows = 4;
	/* 行高度 */
	private int rowHeight = 21;
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

	public int getRowHeight() {
		return rowHeight;
	}

	public T setRowHeight(final int rowHeight) {
		this.rowHeight = rowHeight;
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

	public T setVal(final Object val) {
		return setValue(Convert.toString(val));
	}

	public T setValue(final PageRequestResponse rRequest) {
		return setText(rRequest.getParameter(getName()));
	}

	protected boolean isTextReadonly() {
		return isReadonly();
	}

	@Override
	protected void doAttri(final StringBuilder sb) {
		if (!isDisabled()) {
			addAttribute("onchange", toEventString(getOnchange()));
		} else {
			sb.append(" disabled");
		}

		final EInputType type = getInputType();
		if (isTextReadonly()) {
			sb.append(" readonly");
		}
		if (type == EInputType.checkbox || type == EInputType.radio) {
			if (isChecked()) {
				sb.append(" checked");
			}
		}
		if (type == EInputType.textarea) {
			addAttribute("rows", getRows());
			if (isAutoRows()) {
				final int rowHeight = getRowHeight();
				final int height = rowHeight * getRows() - 4;
				addStyle("overflow-y:hidden; min-height: " + height + "px; height: " + height
						+ "px; line-height: " + rowHeight + "px;");
				addAttribute("autorows", "true");
				final String js = "this.style.height = (" + rowHeight
						+ " * Math.floor(this.scrollHeight / " + rowHeight + ")) + 'px';";
				addAttribute("oninput", "this.style.height='0px';" + js);
				addAttribute("onfocus", js);
				addAttribute("onpropertychange", js);
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
			sb.append("<input type='").append(type).append("'");
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
