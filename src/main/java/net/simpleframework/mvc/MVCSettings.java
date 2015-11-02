package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.simpleframework.ctx.settings.ContextSettings;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCSettings extends ContextSettings implements IMVCConst {

	public MVCSettings(final IMVCContext context, final ContextSettings applicationSettings) {
		final Map<String, String> packages = getFilterPackages();
		if (packages != null) {
			for (final String key : new ArrayList<String>(packages.keySet())) {
				if (key.endsWith("/")) {
					packages.put(key.substring(0, key.length() - 1), packages.remove(key));
				}
			}
		}

		// 关联到context
		context.setMVCSettings(this);
		// 引用contextSettings
		setApplicationSettings(applicationSettings);
	}

	public MVCSettings(final IMVCContext context) {
		this(context, null);
	}

	/**
	 * response是否压缩
	 * 
	 * @param rRequest
	 * @return
	 */
	public boolean isGzipResponse(final PageRequestResponse rRequest) {
		return isDebug() ? false : true;
	}

	/**
	 * 资源是否压缩，js和css
	 * 
	 * @return
	 */
	public boolean isResourceCompress() {
		return isDebug() ? false : true;
	}

	public int getServerPort(final PageRequestResponse rRequest) {
		return rRequest.getServerPort();
	}

	public boolean isEffect(final PageRequestResponse rRequest) {
		final Float ver = rRequest.getIEVersion();
		return ver == null || ver > 8.0;
	}

	public String getIEWarnPath(final PageRequestResponse rRequest) {
		return MVCUtils.getPageResourcePath() + "/jsp/ie_version_alert.jsp";
	}

	public String getErrorPath(final PageRequestResponse rRequest) {
		return MVCUtils.getPageResourcePath() + "/jsp/error.jsp";
	}

	public String getLoginPath(final PageRequestResponse rRequest) {
		return null;
	}

	/**
	 * 定义首页的url
	 * 
	 * @param rRequest
	 * @return
	 */
	public String getHomePath(final PageRequestResponse rRequest) {
		return null;
	}

	private static Set<String> pKeys = new HashSet<String>(Arrays.asList(new String[] { REQUEST_ID,
			PARAM_XMLPATH }));

	/**
	 * 获取系统请求参数
	 * 
	 * @return
	 */
	public Set<String> getSystemParamKeys() {
		return pKeys;
	}

	/*----------------------------- AbstractMVCPage -----------------------------*/

	/**
	 * 获取拦截Page的根路径
	 * 
	 * @return
	 */
	public String getFilterPath() {
		return "/";
	}

	private final Map<String, String> packages = new LinkedHashMap<String, String>();
	{
		packages.put("/sf", "net.simpleframework");
	}

	public Map<String, String> getFilterPackages() {
		return packages;
	}
}
