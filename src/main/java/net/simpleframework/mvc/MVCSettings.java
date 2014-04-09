package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.Map;

import net.simpleframework.ctx.IApplicationContextBase;
import net.simpleframework.ctx.settings.ContextSettings;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCSettings extends ContextSettings {

	public static MVCSettings getDefault() {
		return singleton(MVCSettings.class);
	}

	@Override
	public void onInit(final IApplicationContextBase context) throws Exception {
		final Map<String, String> packages = getFilterPackages();
		if (packages != null) {
			for (final String key : new ArrayList<String>(packages.keySet())) {
				if (key.endsWith("/")) {
					packages.put(key.substring(0, key.length() - 1), packages.remove(key));
				}
			}
		}
		if (context instanceof IMVCContext) {
			// 设置settings
			((IMVCContext) context).setMVCSettings(this);
		}
	}

	public String getRealPath(final String url) {
		return MVCUtils.getRealPath(url);
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

	private static final String IE_VERSION_PAGE = "/jsp/ie_version_alert.jsp";

	public String getIEWarnPath(final PageRequestResponse rRequest) {
		return MVCUtils.getPageResourcePath() + IE_VERSION_PAGE;
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

	public boolean isEffect(final PageRequestResponse rRequest) {
		final Float ver = rRequest.getIEVersion();
		return ver == null || ver > 8.0;
	}

	public int getServerPort(final PageRequestResponse rRequest) {
		return rRequest.getServerPort();
	}

	/**
	 * 资源是否压缩，js和css
	 * 
	 * @return
	 */
	public boolean isResourceCompress() {
		return isDebug() ? false : true;
	}

	private static final String[] pKeys = new String[] { IMVCConst.REQUEST_ID,
			IMVCConst.PARAM_XMLPATH };

	/**
	 * 获取系统请求参数
	 * 
	 * @return
	 */
	public String[] getSystemParamKeys() {
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

	public Map<String, String> getFilterPackages() {
		return null;
	}
}
