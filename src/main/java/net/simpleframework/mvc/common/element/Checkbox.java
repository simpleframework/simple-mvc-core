package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class Checkbox extends AbstractInputElement<Checkbox> {
	protected LabelElement label;

	public Checkbox(final String id, final Object labelTxt) {
		setId(id).setName(id);
		addStyle("vertical-align: middle;");
		label = new LabelElement(labelTxt).setForId(id).setClassName("checkbox_lbl");
	}

	public LabelElement getLabel() {
		return label;
	}

	@Override
	public EInputType getInputType() {
		return EInputType.checkbox;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		if (label != null) {
			sb.append(label);
		}
		return sb.toString();
	}
}
