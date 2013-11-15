package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class InputElement extends AbstractInputElement<InputElement> {
	public static InputElement textarea() {
		return new InputElement().setInputType(EInputType.textarea);
	}

	public static InputElement textarea(final String id) {
		return new InputElement(id, EInputType.textarea);
	}

	public static InputElement select() {
		return new InputElement().setInputType(EInputType.select);
	}

	public static InputElement select(final String id) {
		return new InputElement(id, EInputType.select);
	}

	public static InputElement hidden(final String id) {
		return new InputElement(id, EInputType.hidden);
	}

	public static InputElement hidden() {
		return new InputElement().setInputType(EInputType.hidden);
	}

	public static InputElement textButton(final String id) {
		return new InputElement(id, EInputType.textButton);
	}

	public static InputElement textButton() {
		return new InputElement().setInputType(EInputType.textButton);
	}

	public static InputElement checkbox() {
		return new InputElement().setInputType(EInputType.checkbox);
	}

	public static InputElement checkbox(final String id) {
		return new InputElement(id, EInputType.checkbox);
	}

	public InputElement() {
	}

	public InputElement(final String id) {
		this(id, null);
	}

	public InputElement(final String id, final EInputType inputType) {
		setId(id);
		setName(id);
		setInputType(inputType);
	}
}
