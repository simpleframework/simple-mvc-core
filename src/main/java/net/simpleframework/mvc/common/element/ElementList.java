package net.simpleframework.mvc.common.element;

import java.util.ArrayList;
import java.util.List;

import net.simpleframework.common.coll.AbstractArrayListEx;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ElementList extends AbstractArrayListEx<ElementList, AbstractElement<?>> {

	public static ElementList of(final AbstractElement<?>... items) {
		return new ElementList().append(items);
	}

	private static final List<IElementFilter> elementFilters = new ArrayList<>();

	public static void addFilter(final IElementFilter elementFilter) {
		elementFilters.add(elementFilter);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size(); i++) {
			final AbstractElement<?> ele = get(i);
			for (final IElementFilter elementFilter : elementFilters) {
				elementFilter.doFilter(ele, this, i);
			}
		}
		for (final AbstractElement<?> ele : this) {
			sb.append(ele);
		}
		return sb.toString();
	}

	public static interface IElementFilter {

		void doFilter(AbstractElement<?> ele, ElementList el, int index);
	}

	private static final long serialVersionUID = -8718459221197045855L;
}
