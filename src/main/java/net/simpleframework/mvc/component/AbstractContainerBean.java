package net.simpleframework.mvc.component;

import net.simpleframework.common.Convert;
import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.mvc.PageDocument;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractContainerBean extends AbstractComponentBean {
	/* 定义容器组件的邦定id,该属性属于必须项 */
	private String containerId;

	/* 定义组件容器的高度和宽度, 默认为auto */
	private String width, height;

	public AbstractContainerBean(final PageDocument pageDocument, final XmlElement xmlElement) {
		super(pageDocument, xmlElement);
	}

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
		final int w = Convert.toInt(width);
		this.width = w > 0 ? w + "px" : width;
		return this;
	}

	public String getHeight() {
		return height;
	}

	public AbstractContainerBean setHeight(final String height) {
		final int h = Convert.toInt(height);
		this.height = h > 0 ? h + "px" : height;
		return this;
	}
}
