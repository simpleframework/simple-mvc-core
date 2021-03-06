package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class Radio extends Checkbox {

	public Radio(final String id, final Object labelTxt) {
		super(id, labelTxt);
	}

	@Override
	public EInputType getInputType() {
		return EInputType.radio;
	}

	private static final long serialVersionUID = 4528757634181971096L;
}
