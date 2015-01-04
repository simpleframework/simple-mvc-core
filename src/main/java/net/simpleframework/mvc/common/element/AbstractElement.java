package net.simpleframework.mvc.common.element;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.AbstractArrayListEx;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.object.TextNamedObject;
import net.simpleframework.common.web.html.HtmlEncoder;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings("unchecked")
public abstract class AbstractElement<T extends AbstractElement<T>> extends TextNamedObject<T> {
	private String id;

	/* 类 */
	private String className;

	/* 禁止 */
	private boolean disabled;

	private String onclick, ondblclick;

	/* 描述 */
	private String title;

	/* 样式 */
	private String style;

	/* 水平对齐方式 */
	private ETextAlign textAlign;

	/* 垂直对齐方式 */
	private EVerticalAlign verticalAlign;

	private String placeholder;

	private boolean strong, italic;

	private String width;

	/* 字体颜色 */
	private String color;

	private Map<String, Object> attributes;

	public String getId() {
		return id;
	}

	public T setId(final String id) {
		this.id = id;
		return (T) this;
	}

	public String getStyle() {
		return style;
	}

	public T setStyle(final String style) {
		this.style = style;
		return (T) this;
	}

	public T addStyle(final String style) {
		final Map<String, String> map = toStyle(getStyle());
		map.putAll(toStyle(style));
		return setStyle(joinStyle(map));
	}

	public String getOnclick() {
		return onclick;
	}

	public T setOnclick(final String onclick) {
		this.onclick = onclick;
		return (T) this;
	}

	public String getOndblclick() {
		return ondblclick;
	}

	public T setOndblclick(final String ondblclick) {
		this.ondblclick = ondblclick;
		return (T) this;
	}

	public String getClassName() {
		return className;
	}

	public T setClassName(final String className) {
		this.className = className;
		return (T) this;
	}

	public T addClassName(final String className) {
		if (!StringUtils.hasText(className)) {
			return (T) this;
		}
		final Set<String> set = toClassSet();
		set.add(className.trim());
		return setClassName(StringUtils.join(set, " "));
	}

	public T removeClassName(final String className) {
		if (!StringUtils.hasText(className)) {
			return (T) this;
		}
		final Set<String> set = toClassSet();
		set.remove(className.trim());
		return setClassName(StringUtils.join(set, " "));
	}

	private Set<String> toClassSet() {
		final Set<String> set = new LinkedHashSet<String>();
		for (final String s : StringUtils.split(getClassName(), " ")) {
			set.add(s.trim());
		}
		return set;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public T setDisabled(final boolean disabled) {
		this.disabled = disabled;
		return (T) this;
	}

	public String getTitle() {
		return title;
	}

	public T setTitle(final String title) {
		this.title = title;
		return (T) this;
	}

	public ETextAlign getTextAlign() {
		return textAlign;
	}

	public T setTextAlign(final ETextAlign textAlign) {
		this.textAlign = textAlign;
		if (textAlign != null) {
			addStyle("text-align: " + textAlign);
		}
		return (T) this;
	}

	public EVerticalAlign getVerticalAlign() {
		return verticalAlign;
	}

	public T setVerticalAlign(final EVerticalAlign verticalAlign) {
		this.verticalAlign = verticalAlign;
		if (verticalAlign != null) {
			addStyle("vertical-align: " + verticalAlign);
		}
		return (T) this;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public T setPlaceholder(final String placeholder) {
		this.placeholder = placeholder;
		return (T) this;
	}

	public boolean isStrong() {
		return strong;
	}

	public T setStrong(final boolean strong) {
		this.strong = strong;
		if (strong) {
			addStyle("font-weight: bold;");
		}
		return (T) this;
	}

	public boolean isItalic() {
		return italic;
	}

	public T setItalic(final boolean italic) {
		this.italic = italic;
		if (italic) {
			addStyle("font-style: italic;");
		}
		return (T) this;
	}

	public String getWidth() {
		return width;
	}

	public T setWidth(final String width) {
		this.width = width;
		if (StringUtils.hasText(width)) {
			addStyle("width: " + width);
		}
		return (T) this;
	}

	public String getColor() {
		return color;
	}

	public T setColor(final String color) {
		this.color = color;
		if (StringUtils.hasText(color)) {
			addStyle("color: " + color);
		}
		return (T) this;
	}

	public T addElements(final AbstractElement<?>... elements) {
		final StringBuilder sb = new StringBuilder();
		final String txt = getText();
		if (StringUtils.hasText(txt)) {
			sb.append(txt);
		}
		for (final AbstractElement<?> element : elements) {
			sb.append(element);
		}
		setText(sb.toString());
		return (T) this;
	}

	public T addHtml(final String html) {
		if (html != null) {
			final StringBuilder sb = new StringBuilder();
			final String txt = getText();
			if (StringUtils.hasText(txt)) {
				sb.append(txt);
			}
			sb.append(html);
			setText(sb.toString());
		}
		return (T) this;
	}

	public T addElements(final AbstractArrayListEx<?, ?> elements) {
		final StringBuilder sb = new StringBuilder();
		final String txt = getText();
		if (StringUtils.hasText(txt)) {
			sb.append(txt);
		}
		if (elements != null) {
			sb.append(elements.toString());
		}
		setText(sb.toString());
		return (T) this;
	}

	public T addAttribute(final String key, final Object val) {
		if (attributes == null) {
			attributes = new KVMap();
		}
		attributes.put(key, val);
		return (T) this;
	}

	protected void doAttri(final StringBuilder sb) {
		addAttribute("id", getId()).addAttribute("name", getName());
		addAttribute("title", getTitle()).addAttribute("placeholder", getPlaceholder());

		addAttribute("class", getClassName());
		if (!isDisabled()) {
			addAttribute("onclick", getOnclick()).addAttribute("ondblclick", getOndblclick());
		}

		addAttribute("style", getStyle());

		for (final Map.Entry<String, Object> e : attributes.entrySet()) {
			final Object val = e.getValue();
			if (val == null) {
				continue;
			}
			sb.append(" ").append(e.getKey()).append("=\"");
			sb.append(HtmlEncoder.text(Convert.toString(val))).append("\"");
		}
	}

	@Override
	public T clone() {
		return (T) BeanUtils.clone(this);
	}

	public static String joinStyle(final Map<String, String> styles) {
		if (styles != null && styles.size() > 0) {
			final StringBuilder sb = new StringBuilder();
			for (final Map.Entry<String, String> e : styles.entrySet()) {
				if (sb.length() > 0) {
					sb.append(";");
				}
				sb.append(e.getKey()).append(":").append(e.getValue());
			}
			return sb.toString();
		}
		return null;
	}

	public static Map<String, String> toStyle(final String style) {
		final Map<String, String> styles = new LinkedHashMap<String, String>();
		for (final String s : StringUtils.split(style, ";")) {
			final String[] arr = StringUtils.split(s.toLowerCase(), ":");
			if (arr.length > 0) {
				styles.put(arr[0].trim(), arr.length > 1 ? arr[1].trim() : "");
			}
		}
		return styles;
	}
}
