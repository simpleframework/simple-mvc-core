package net.simpleframework.mvc.common;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.I18n;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.common.xml.AbstractElementBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings("unchecked")
public abstract class ItemUIBean<T extends ItemUIBean<T>> extends AbstractElementBean {
	private String id;

	private String text;

	private String tooltip;

	private boolean select;

	// event
	private String jsClickCallback, jsDblclickCallback;

	private final Object data;

	public ItemUIBean(final Object data) {
		this.data = data;

		if (BeanUtils.hasProperty(data, "id")) {
			setId(Convert.toString(BeanUtils.getProperty(data, "id")));
		} else if (data instanceof Enum<?>) {
			setId(((Enum<?>) data).name());
		}
		setText(Convert.toString(data));
		if (BeanUtils.hasProperty(data, "description")) {
			setTooltip(Convert.toString(BeanUtils.getProperty(data, "description")));
		}
	}

	public String getId() {
		if (!StringUtils.hasText(id)) {
			id = ObjectUtils.hashStr(getText());
		}
		return id;
	}

	public T setId(final String id) {
		this.id = id;
		return (T) this;
	}

	public String getText() {
		return I18n.replaceI18n(text);
	}

	public T setText(final String text) {
		this.text = text;
		return (T) this;
	}

	public String getTooltip() {
		return tooltip;
	}

	public T setTooltip(final String tooltip) {
		this.tooltip = tooltip;
		return (T) this;
	}

	public boolean isSelect() {
		return select;
	}

	public T setSelect(final boolean select) {
		this.select = select;
		return (T) this;
	}

	public String getJsClickCallback() {
		return jsClickCallback;
	}

	public T setJsClickCallback(final String jsClickCallback) {
		this.jsClickCallback = jsClickCallback;
		return (T) this;
	}

	public String getJsDblclickCallback() {
		return jsDblclickCallback;
	}

	public T setJsDblclickCallback(final String jsDblclickCallback) {
		this.jsDblclickCallback = jsDblclickCallback;
		return (T) this;
	}

	public Object getDataObject() {
		return data;
	}

	@Override
	protected String[] elementAttributes() {
		return new String[] { "jsClickCallback", "jsDblclickCallback" };
	}
}
