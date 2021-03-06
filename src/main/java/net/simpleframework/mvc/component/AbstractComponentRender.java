package net.simpleframework.mvc.component;

import java.io.Writer;

import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.th.NotImplementedException;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.common.web.JavascriptUtils;
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.IMVCSettingsAware;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.common.element.JS;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractComponentRender extends ObjectEx
		implements IComponentRender, IMVCSettingsAware {
	private IComponentRegistry componentRegistry;

	@Override
	public IComponentRegistry getComponentRegistry() {
		return componentRegistry;
	}

	public void setComponentRegistry(final IComponentRegistry componentRegistry) {
		this.componentRegistry = componentRegistry;
	}

	public static abstract class ComponentJavascriptRender extends AbstractComponentRender
			implements IComponentJavascriptRender {
	}

	public static abstract class ComponentBaseActionJavascriptRender
			extends ComponentJavascriptRender {

		protected abstract String getParams(ComponentParameter cp);

		protected abstract String getActionPath(ComponentParameter cp);

		@Override
		public String getJavascriptCode(final ComponentParameter cp) {
			final StringBuilder sb = new StringBuilder();
			sb.append("var dc = function() { $Loading.hide(); };");
			sb.append("$Loading.show();");
			sb.append("var params=\"").append(StringUtils.blank(getParams(cp))).append("\";");
			ComponentRenderUtils.appendParameters(sb, cp, "params");
			sb.append("params = params.addParameter(arguments[0]);");
			sb.append(ComponentRenderUtils.actionFunc(cp)).append(".params = params;");
			sb.append("new Ajax.Request('").append(getActionPath(cp)).append("', {");
			sb.append("postBody: params,");
			sb.append("onComplete: function(req) {");
			sb.append("try { $call(req.responseText); } finally { dc(); }");
			sb.append("}, onException: dc, onFailure: dc");
			sb.append("});");
			return ComponentRenderUtils.genActionWrapper(cp, sb.toString());
		}
	}

	public static void doJavascriptForward(final ComponentParameter cp,
			final IJavascriptCallback callback) throws Exception {
		final JavascriptForward js = new JavascriptForward();
		cp.setHttpRequest();
		final IForward forward = cp.getPermission().accessForward(cp,
				cp.componentBean != null ? cp.getBeanProperty("role")
						: PermissionConst.ROLE_ALL_ACCOUNT);
		if (forward instanceof UrlForward) {
			js.append(JS.loc(((UrlForward) forward).getUrl()));
		} else {
			if (callback != null) {
				callback.doJavascript(js);
			}
		}
		final Writer out = cp.getResponseWriter();
		out.write(JavascriptUtils.wrapFunction(js.toString()));
		out.flush();
	}

	public static interface IJavascriptCallback {

		void doJavascript(JavascriptForward js) throws Exception;
	}

	public static abstract class ComponentHtmlRender extends AbstractComponentRender
			implements IComponentHtmlRender {
		@Override
		public String getHtml(final ComponentParameter cp) {
			final IForward forward = getResponseForward(cp);
			if (forward instanceof UrlForward) {
				((UrlForward) forward)
						.setIncludeRequestData((String) cp.getBeanProperty("includeRequestData"));
			}
			return forward.getResponseText(cp);
		}

		public IForward getResponseForward(final ComponentParameter cp) {
			final String url = ComponentUtils.getResourceHomePath(cp.componentBean.getClass())
					+ HttpUtils.addParameters(getRelativePath(cp),
							(String) cp.getBeanProperty("parameters"));
			return new UrlForward(url);
		}

		protected String getRelativePath(final ComponentParameter cp) {
			throw NotImplementedException.of(getClass(), "getRelativePath");
		}
	}
}