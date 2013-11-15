package net.simpleframework.mvc.common.element;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class LabelElement extends AbstractTagElement<LabelElement> {

	private String forId;

	public LabelElement() {
	}

	public LabelElement(final Object text) {
		super(text);
	}

	public String getForId() {
		return forId;
	}

	public LabelElement setForId(final String forId) {
		this.forId = forId;
		return this;
	}

	public LabelElement setFor(final AbstractInputElement<?> element) {
		String id = element.getId();
		if (!StringUtils.hasText(id)) {
			id = "element_" + ObjectUtils.hashStr(this);
			element.setId(id);
		}
		return setForId(id);
	}

	@Override
	protected String tag() {
		return "label";
	}

	@Override
	protected void doAttri(final StringBuilder sb) {
		super.doAttri(sb);
		String forId;
		if (StringUtils.hasText(forId = getForId())) {
			sb.append(" for=\"").append(forId).append("\"");
		}
	}
}
