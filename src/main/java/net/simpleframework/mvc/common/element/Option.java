package net.simpleframework.mvc.common.element;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.common.Convert;
import net.simpleframework.common.object.NamedObject;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class Option extends AbstractElement<Option> {

	public static Option TRUE() {
		return new Option("true", $m("Yes"));
	}

	public static Option FALSE() {
		return new Option("false", $m("No"));
	}

	public static Option[] from(final Enum<?>... vals) {
		final Option[] opts = new Option[vals.length];
		for (int i = 0; i < opts.length; i++) {
			final Enum<?> e = vals[i];
			opts[i] = new Option(e.name(), e.toString());
		}
		return opts;
	}

	public static Option[] from(final NamedObject<?>... vals) {
		final Option[] opts = new Option[vals.length];
		for (int i = 0; i < opts.length; i++) {
			final NamedObject<?> e = vals[i];
			opts[i] = new Option(e.getName(), e.toString());
		}
		return opts;
	}

	public static Option[] from(final String... vals) {
		final Option[] opts = new Option[vals.length];
		for (int i = 0; i < opts.length; i++) {
			opts[i] = new Option(vals[i]);
		}
		return opts;
	}

	public static void setSelected(final Option[] opts, final Enum<?> val) {
		setSelected(opts, val.name());
	}

	public static void setSelected(final Option[] opts, final String val) {
		if (opts != null && val != null) {
			for (final Option opt : opts) {
				if (val.equals(opt.getName())) {
					opt.setSelected(true);
					break;
				}
			}
		}
	}

	private boolean selected;

	public Option() {
	}

	public Option(final Object name) {
		this(name, name);
	}

	public Option(final Object name, final Object text) {
		setText(Convert.toString(text)).setName(Convert.toString(name));
	}

	public Option(final NamedObject<?> o) {
		this(o.getName(), o.toString());
	}

	public boolean isSelected() {
		return selected;
	}

	public Option setSelected(final boolean selected) {
		this.selected = selected;
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("<option");
		final String name = getName();
		if (name != null) {
			sb.append(" value='").append(name).append("'");
		}
		if (isSelected()) {
			sb.append(" selected='selected'");
		}
		sb.append(">").append(getText()).append("</option>");
		return sb.toString();
	}

	private static final long serialVersionUID = 1242002392537742316L;
}
