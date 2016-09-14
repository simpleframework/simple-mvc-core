package net.simpleframework.mvc.parser;

import java.util.Map;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.IPageHandler.PageSelector;
import net.simpleframework.mvc.PageParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageLoaded {

	void doTag(final PageParameter pp, final Element htmlHead, final Map<String, Object> dataBinding,
			final PageSelector selector) {
		final KVMap kv = new KVMap();
		if (dataBinding.size() > 0) {
			kv.add("dataBinding", dataBinding);
		}
		if (StringUtils.hasText(selector.visibleToggleSelector)) {
			kv.add("visibleToggle", selector.visibleToggleSelector);
		}
		if (StringUtils.hasText(selector.readonlySelector)) {
			kv.add("readonly", selector.readonlySelector);
		}
		if (StringUtils.hasText(selector.disabledSelector)) {
			kv.add("disabled", selector.disabledSelector);
		}
		final StringBuilder sb = new StringBuilder();
		if (kv.size() > 0) {
			sb.append("var json = ").append(kv.toJSON()).append(";");
		}
		final String jsLoadedCallback = pp.getPageDocument().getJsLoadedCallback(pp);
		if (StringUtils.hasText(jsLoadedCallback)) {
			sb.append(jsLoadedCallback);
		} else if (kv.size() > 0) {
			if (dataBinding.size() > 0) {
				sb.append("$Actions.valueBinding(json.dataBinding);");
			}
			if (StringUtils.hasText(selector.visibleToggleSelector)) {
				sb.append("$$(json.visibleToggle).each(function(e) { $Actions.visibleToggle(e); });");
			}
			if (StringUtils.hasText(selector.readonlySelector)) {
				sb.append("$$(json.readonly).each(function(e) { $Actions.readonly(e); });");
			}
			if (StringUtils.hasText(selector.disabledSelector)) {
				sb.append("$$(json.disabled).each(function(e) { $Actions.disable(e); });");
			}
		}

		if (sb.length() > 0) {
			ParserUtils.addScriptText(htmlHead, JavascriptUtils.wrapWhenReady(sb.toString()));
		}
	}
}
