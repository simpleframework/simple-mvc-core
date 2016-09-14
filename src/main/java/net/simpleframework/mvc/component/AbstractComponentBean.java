package net.simpleframework.mvc.component;

import java.io.IOException;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.common.bean.BeanDefaults;
import net.simpleframework.ctx.common.xml.AbstractElementBean;
import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.mvc.IMVCSettingsAware;
import net.simpleframework.mvc.PageDocument;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentBean extends AbstractElementBean
		implements IMVCSettingsAware {
	public static final String FORM_PREFIX = "form_";

	private PageDocument pageDocument;

	/*
	 * 组件的名称. 在一次请求中要求唯一.
	 * 
	 * 如果存在js对象,浏览器端可以通过$Actions[name]获取该组件的js对象
	 */
	protected String name;

	/* 定义组件是否立即运行,如果定义false,则需要通过代码的方式运行,比如: $Actions[name](); */
	private boolean runImmediately = BeanDefaults.getBool(getClass(), "runImmediately", true);

	/* 定义组件的业务处理类hanlder */
	private String handlerClass;

	/* 定义处理类hanlder实例的生命周期 */
	private EComponentHandlerScope handleScope = (EComponentHandlerScope) BeanDefaults
			.get(getClass(), "handleScope", EComponentHandlerScope.singleton);

	/* 定义组件的CSS选择器, 具体含义与实现组件有关, 比如AjaxRequest的selector定义了提交的数据 */
	protected String selector;

	/*
	 * 组件可获取的request数据, 值为pah, p=parameter a=attribute h=header
	 * 
	 * 比如需要获取request的头数据和参数数据,则includeRequestData=hp
	 */
	private String includeRequestData;

	/* 传入组件的参数 */
	private String parameters;

	/* 是否显示效果 */
	private boolean effects = BeanDefaults.getBool(getClass(), "effects", true);

	public PageDocument getPageDocument() {
		return pageDocument;
	}

	public AbstractComponentBean setPageDocument(final PageDocument pageDocument) {
		this.pageDocument = pageDocument;
		return this;
	}

	public void saveToFile() throws IOException {
		syncElement();
		final PageDocument pageDocument = getPageDocument();
		pageDocument.saveToFile(pageDocument.getDocumentFile());
	}

	public String getName() {
		return name != null ? name.trim() : null;
	}

	public AbstractComponentBean setName(final String name) {
		this.name = name;
		return this;
	}

	public boolean isRunImmediately() {
		return runImmediately;
	}

	public AbstractComponentBean setRunImmediately(final boolean runImmediately) {
		this.runImmediately = runImmediately;
		return this;
	}

	public String getHandlerClass() {
		return handlerClass;
	}

	public AbstractComponentBean setHandlerClass(final String handlerClass) {
		this.handlerClass = handlerClass;
		return this;
	}

	public AbstractComponentBean setHandlerClass(final Class<?> hClass) {
		return setHandlerClass(ObjectFactory.original(hClass).getName());
	}

	public EComponentHandlerScope getHandleScope() {
		return handleScope;
	}

	public AbstractComponentBean setHandleScope(final EComponentHandlerScope handleScope) {
		this.handleScope = handleScope;
		return this;
	}

	public String getSelector() {
		if (StringUtils.hasText(selector)) {
			// sb.append("#").append(AbstractComponentBean.FORM_PREFIX).append(hashId());
			// sb.append(", ");
			return selector;
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append("#").append(AbstractComponentBean.FORM_PREFIX).append(hashId());
			return sb.toString();
		}
	}

	public AbstractComponentBean setSelector(final String selector) {
		this.selector = selector;
		return this;
	}

	public boolean isEffects() {
		return effects;
	}

	public AbstractComponentBean setEffects(final boolean effects) {
		this.effects = effects;
		return this;
	}

	public String getParameters() {
		return parameters;
	}

	public AbstractComponentBean setParameters(final String parameters) {
		this.parameters = parameters;
		return this;
	}

	public String getIncludeRequestData() {
		return includeRequestData;
	}

	public AbstractComponentBean setIncludeRequestData(final String includeRequestData) {
		this.includeRequestData = includeRequestData;
		return this;
	}

	public IComponentHandler getComponentHandler(final PageRequestResponse rRequest) {
		return ComponentUtils.getComponentHandler(rRequest, this);
	}

	private String _componentHashId;

	public String hashId() {
		if (_componentHashId == null) {
			String name = getName();
			if (!StringUtils.hasText(name)) {
				name = getElement().attributeValue("name");
			}
			_componentHashId = StringUtils.hasText(name)
					? ComponentUtils.getComponentHashByName(pageDocument, name)
					: ObjectUtils.hashStr(this);
		}
		return _componentHashId;
	}

	public IComponentRegistry getComponentRegistry() {
		return AbstractComponentRegistry.getComponentRegistry(getClass());
	}

	@Override
	public XmlElement getElement() {
		XmlElement element = super.getElement();
		if (element == null) {
			setElement(element = getPageDocument().getRoot());
		}
		return element;
	}
}
