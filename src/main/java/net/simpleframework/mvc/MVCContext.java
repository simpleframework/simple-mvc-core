package net.simpleframework.mvc;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.ClassUtils.IScanResourcesCallback;
import net.simpleframework.common.ClassUtils.ScanClassResourcesCallback;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.AbstractApplicationContextBase;
import net.simpleframework.ctx.permission.IPermissionHandler;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.ctx.script.ScriptEvalFactory;
import net.simpleframework.mvc.IPageResourceProvider.MVCPageResourceProvider;
import net.simpleframework.mvc.common.DeployWeb;
import net.simpleframework.mvc.component.AbstractComponentRegistry;
import net.simpleframework.mvc.ctx.permission.DefaultPagePermissionHandler;
import net.simpleframework.mvc.ctx.permission.IPagePermissionHandler;
import net.simpleframework.mvc.ctx.permission.PermissionFilterListener;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCContext extends AbstractApplicationContextBase implements IMVCContext {

	private static MVCContext mvcContext;

	public static MVCContext get() {
		return mvcContext;
	}

	private ServletContext servletContext;

	private MVCSettings settings;

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	protected void onBeforeInit() throws Exception {
		super.onBeforeInit();
		mvcContext = this;

		// servlet 3.0
		if (servletContext.getMajorVersion() >= 3) {
			// servletContext.addListener(MVCEventAdapter.class.getName());
		}

		getEventAdapter().addListener(SessionCache.SESSIONCACHE_LISTENER);
	}

	@Override
	protected void onAfterInit() throws Exception {
		super.onAfterInit();

		addFilterListener(new UtilsFilterListener());
		addFilterListener(new LastUrlListener());
		addFilterListener(new PermissionFilterListener());
	}

	@Override
	protected void doInternalInit(final String[] packageNames) throws Exception {
		super.doInternalInit(packageNames);
		// 资源
		IScanResourcesCallback callback = DeployWeb.newDeployResourcesCallback();
		for (final String packageName : packageNames) {
			ClassUtils.scanResources(packageName, callback);
		}

		// 组件
		callback = AbstractComponentRegistry.newComponentRegistryCallback();
		for (final String packageName : packageNames) {
			ClassUtils.scanResources(packageName, callback);
		}

		// MVCPageResourceProvider
		for (final String packageName : packageNames) {
			ClassUtils.scanResources(packageName, new ScanClassResourcesCallback() {
				@SuppressWarnings("unchecked")
				@Override
				public void doResources(final String filepath, final boolean isDirectory)
						throws Exception {
					final Class<?> rClass = loadClass(filepath);
					if (rClass == null) {
						return;
					}
					final MVCPageResourceProvider provider = getInstance(rClass,
							MVCPageResourceProvider.class);
					if (provider != null) {
						if (defaultPageResourceProvider == null
								|| defaultPageResourceProvider.getClass().isAssignableFrom(
										provider.getClass())) {
							defaultPageResourceProvider = provider;
						}
					}
					if (AbstractMVCPage.class.isAssignableFrom(rClass)
							&& !Modifier.isAbstract(rClass.getModifiers())) {
						final PageMapping mapping = rClass.getAnnotation(PageMapping.class);
						String url;
						if (mapping != null && StringUtils.hasText(url = mapping.url())) {
							AbstractMVCPage.registUrl(url, (Class<? extends AbstractMVCPage>) rClass,
									mapping.priority());
						}
					}
				}
			});
		}
	}

	// 过滤监听器
	private final ArrayList<IFilterListener> filterColl = new ArrayList<IFilterListener>();

	@Override
	public Collection<IFilterListener> getFilterListeners() {
		return filterColl;
	}

	@Override
	public void addFilterListener(final IFilterListener listener) {
		filterColl.add(listener);
	}

	@Override
	protected Class<? extends IPermissionHandler> getPagePermissionHandler() {
		return DefaultPagePermissionHandler.class;
	}

	@Override
	public IScriptEval createScriptEval(final Map<String, Object> variables) {
		return ScriptEvalFactory.createDefaultScriptEval(variables);
	}

	private IPageResourceProvider defaultPageResourceProvider;

	@Override
	public IPageResourceProvider getDefaultPageResourceProvider() {
		return defaultPageResourceProvider;
	}

	@Override
	public IMultipartPageRequest createMultipartPageRequest(final HttpServletRequest request,
			final int maxUploadSize) throws IOException {
		return new MultipartPageRequest(request, maxUploadSize);
	}

	@Override
	public MVCHtmlBuilder getPageHtmlBuilder() {
		return singleton(MVCHtmlBuilder.class);
	}

	@Override
	public HttpSession createHttpSession(final HttpSession httpSession) {
		return httpSession instanceof PageSession ? httpSession : new PageSession(httpSession);
	}

	@Override
	public MVCEventAdapter getEventAdapter() {
		return singleton(MVCEventAdapter.class);
	}

	@Override
	public MVCSettings getMVCSettings() {
		return settings;
	}

	@Override
	public void setMVCSettings(final MVCSettings settings) {
		this.settings = settings;
	}

	@Override
	public IPagePermissionHandler getPermission() {
		return (IPagePermissionHandler) super.getPermission();
	}

	@Override
	public boolean isSystemUrl(final PageRequestResponse rRequest) {
		final String requestURI = rRequest.getRequestURI();
		if (requestURI.endsWith(".jsp") || requestURI.contains("/favicon.ico")) {
			return true;
		}
		if (requestURI.indexOf(MVCConst.JSESSIONID + "=") > -1) {
			return true;
		}
		final String lPath = getMVCSettings().getLoginPath(rRequest);
		if (StringUtils.hasText(lPath) && requestURI.indexOf(lPath) > -1) {
			return true;
		}
		return false;
	}
}
