package net.simpleframework.mvc.component;

import java.util.Map;

import net.simpleframework.common.JsonUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.mvc.AbstractUrlForward;
import net.simpleframework.mvc.common.element.InputElement;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class ComponentRenderUtils {

	public static String genActionWrapper(final ComponentParameter cp, final String wrapCode) {
		return genActionWrapper(cp, wrapCode, null);
	}

	public static String genActionWrapper(final ComponentParameter cp, final String wrapCode,
			final String execCode) {
		return genActionWrapper(cp, wrapCode, execCode, true,
				(Boolean) cp.getBeanProperty("runImmediately"));
	}

	public static String genActionWrapper(final ComponentParameter cp, final String wrapCode,
			final String execCode, final boolean genJSON, final boolean runImmediately) {
		final StringBuilder sb = new StringBuilder();
		final String actionFunc = actionFunc(cp);
		sb.append("var ").append(actionFunc).append("=function() {");
		sb.append(StringUtils.blank(wrapCode)).append("return true;");
		sb.append("};");

		String json;
		if (genJSON && (json = genJSON(cp, actionFunc)) != null) {
			sb.append(json);
		}

		if (StringUtils.hasText(execCode)) {
			sb.append(JavascriptUtils.wrapFunction(execCode));
		}

		sb.append("$Actions[\"").append(cp.getComponentName()).append("\"]=").append(actionFunc)
				.append(";");
		if (runImmediately) {
			sb.append(JavascriptUtils.wrapWhenReady(actionFunc + "();"));
		}
		return sb.toString();
	}

	public static String genJSON(final ComponentParameter cp, final String actionFunc) {
		final Map<String, Object> json;
		final IComponentHandler handle = cp.getComponentHandler();
		if (handle != null && (json = handle.toJSON(cp)) != null) {
			final StringBuilder sb = new StringBuilder();
			sb.append(actionFunc).append(".json=").append(JsonUtils.toJSON(json)).append(";");
			return sb.toString();
		}
		return null;
	}

	public static String actionFunc(final ComponentParameter cp) {
		return "f_" + cp.hashId();
	}

	public static String VAR_CONTAINER = "c";

	public static String initContainerVar(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		sb.append("var ").append(VAR_CONTAINER).append("=").append(actionFunc(cp))
				.append(".container");
		final String containerId = (String) cp.getBeanProperty("containerId");
		if (StringUtils.hasText(containerId)) {
			sb.append(" || $(\"").append(containerId).append("\")");
		}
		sb.append("; if (!").append(VAR_CONTAINER).append(") return;");
		return sb.toString();
	}

	public static void appendParameters(final StringBuilder sb, final ComponentParameter cp,
			final String strVar) {
		// 优先级：requestData < selector < parameters
		final String includeRequestData = (String) cp.getBeanProperty("includeRequestData");
		// if (StringUtils.hasText(includeRequestData)) {
		sb.append(strVar).append(" = ").append(strVar).append(".addParameter(\"");
		sb.append(AbstractUrlForward.putRequestData(cp, includeRequestData)).append("\");");
		// }
		sb.append(strVar).append(" = ").append(strVar).append(".addSelectorParameter(");
		sb.append(actionFunc(cp)).append(".selector");
		final String selector = (String) cp.getBeanProperty("selector");
		if (StringUtils.hasText(selector)) {
			sb.append(" || \"").append(selector).append("\"");
		}
		sb.append(");");
		final String parameters = (String) cp.getBeanProperty("parameters");
		if (StringUtils.hasText(parameters)) {
			sb.append(strVar).append(" = ").append(strVar).append(".addParameter(\"");
			sb.append(parameters).append("\");");
		}
	}

	public static String genParameters(final ComponentParameter cp) {
		final StringBuilder sb = new StringBuilder();
		final Map<String, Object> params = ComponentUtils.toFormParameters(cp);
		if (params != null && params.size() > 0) {
			sb.append("<div class=\"parameters\" id=\"").append(AbstractComponentBean.FORM_PREFIX);
			sb.append(cp.hashId()).append("\">");
			for (final Map.Entry<String, Object> entry : params.entrySet()) {
				sb.append(InputElement.hidden(entry.getKey()).setText(String.valueOf(entry.getValue())));
			}
			sb.append("</div>");
		}
		return sb.toString();
	}
}
