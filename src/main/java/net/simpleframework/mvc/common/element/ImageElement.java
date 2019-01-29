package net.simpleframework.mvc.common.element;

import net.simpleframework.common.Base64;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ImageElement extends AbstractElement<ImageElement> {

	public static ImageElement PNG(final byte[] bytes) {
		return new ImageElement("data:image/png;base64," + Base64.encodeToString(bytes));
	}

	public static ImageElement img16(final String src) {
		return new ImageElement(src).setStyle("width:16px; height:16px;");
	}

	private String src;

	public ImageElement() {
	}

	public ImageElement(final String src) {
		setSrc(src);
	}

	public String getSrc() {
		return src;
	}

	public ImageElement setSrc(final String src) {
		this.src = src;
		return this;
	}

	@Override
	public String toString() {
		addAttribute("src", getSrc());
		final StringBuilder sb = new StringBuilder();
		sb.append("<img");
		doAttri(sb);
		sb.append(" />");
		return sb.toString();
	}

	private static final long serialVersionUID = 1250048699951644877L;
}
