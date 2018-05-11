package net.simpleframework.mvc.component;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings("serial")
public abstract class AbstractContainerBean extends AbstractComponentBean {
	/* 定义容器组件的邦定id,该属性属于必须项 */
	private String containerId;

	/* 定义组件容器的高度和宽度, 默认为auto */
	private String width, height;

	public String getContainerId() {
		return containerId;
	}

	public AbstractContainerBean setContainerId(final String containerId) {
		this.containerId = containerId;
		return this;
	}

	public String getWidth() {
		return width;
	}

	public AbstractContainerBean setWidth(final String width) {
		int w = 0;
		try {
			w = Integer.parseInt(width);
		} catch (final NumberFormatException e) {
		}
		this.width = w > 0 ? w + "px" : width;
		return this;
	}

	public String getHeight() {
		return height;
	}

	public AbstractContainerBean setHeight(final String height) {
		int h = 0;
		try {
			h = Integer.parseInt(height);
		} catch (final NumberFormatException e) {
		}
		this.height = h > 0 ? h + "px" : height;
		return this;
	}
}
