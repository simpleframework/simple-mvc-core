package net.simpleframework.mvc.common.element;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.AbstractArrayListEx;
import net.simpleframework.common.object.TextNamedObject;
import net.simpleframework.common.web.html.HtmlEncoder;
import net.simpleframework.common.web.html.HtmlUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@SuppressWarnings({ "unchecked", "serial" })
public abstract class AbstractElement<T extends AbstractElement<T>> extends TextNamedObject<T>
		implements java.io.Serializable {
	private String id;

	/* 类 */
	private String className;

	/* 禁止 */
	private boolean disabled;

	private String onclick, ondblclick;
	/* 禁止事件冒泡 */
	private boolean eventStopped;

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

	public String getId() {
		return id;
	}

	public T setId(final String id) {
		// if (id != null && !id.matches("^.*[A-Z_]+.*$")) {
		// throw ParserException.of("id值至少要含有一个大写字母！");
		// }
		this.id = id;
		return (T) this;
	}

	public String getStyle() {
		return style;
	}

	public static void main(final String[] args) {
		System.out.println("pager_@bid".matches("^.*[A-Z_]+.*$"));
	}

	public T setStyle(final String style) {
		this.style = style;
		return (T) this;
	}

	public T addStyle(final String style) {
		final Map<String, String> map = HtmlUtils.toStyle(getStyle());
		map.putAll(HtmlUtils.toStyle(style));
		return setStyle(HtmlUtils.joinStyle(map));
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

	public boolean isEventStopped() {
		return eventStopped;
	}

	public T setEventStopped(final boolean eventStopped) {
		this.eventStopped = eventStopped;
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
		final Set<String> set = new LinkedHashSet<>();
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

	public T setColor_gray(final boolean gray) {
		return gray ? setColor("#999") : (T) this;
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

	public T addAttribute(final String key) {
		return addAttribute(key, "");
	}

	protected String toEventString(final String event) {
		if (event != null && isEventStopped()) {
			final StringBuilder sb = new StringBuilder(event);
			if (!event.trim().endsWith(";")) {
				sb.append(";");
			}
			sb.append("Event.stop(event);");
			return sb.toString();
		}
		return event;
	}

	protected void doAttri(final StringBuilder sb) {
		addAttribute("id", getId()).addAttribute("name", getName());
		addAttribute("title", getTitle()).addAttribute("placeholder", getPlaceholder());

		addAttribute("class", getClassName());
		if (!isDisabled()) {
			addAttribute("onclick", toEventString(getOnclick())).addAttribute("ondblclick",
					toEventString(getOndblclick()));
		}

		addAttribute("style", getStyle());

		if (attributes != null) {
			for (final Map.Entry<String, Object> e : attributes.entrySet()) {
				final Object val = e.getValue();
				if (val == null) {
					continue;
				}
				sb.append(" ").append(e.getKey());
				final String sval = Convert.toString(val);
				if (StringUtils.hasText(sval)) {
					sb.append("=\"");
					sb.append(HtmlEncoder.text(sval)).append("\"");
				}
			}
		}
	}

	@Override
	public T clone() {
		return (T) BeanUtils.clone(this);
	}
}
