package net.simpleframework.mvc.ctx.permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.simpleframework.common.FileUtils;
import net.simpleframework.common.ID;
import net.simpleframework.common.ImageUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.th.NotImplementedException;
import net.simpleframework.ctx.permission.DefaultPermissionHandler;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.IMVCContextVar;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;
import net.simpleframework.mvc.UrlForward;
import net.simpleframework.mvc.component.ComponentParameter;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class DefaultPagePermissionHandler extends DefaultPermissionHandler implements
		IPagePermissionHandler, IMVCContextVar {
	@Override
	public PermissionUser getLogin(final PageRequestResponse rRequest) {
		return getUser(getLoginId(rRequest));
	}

	@Override
	public String getLoginRedirectUrl(final PageRequestResponse rRequest, final String role) {
		// 已经登录
		if (getLoginId(rRequest) != null) {
			return null;
		}
		// 不存在角色或角色是匿名
		if (!StringUtils.hasText(role) || ROLE_ANONYMOUS.equals(role)) {
			return null;
		}

		final String rPath = MVCUtils.getPageResourcePath();
		if (rRequest.getRequestURI().contains(rPath + "/jsp/login_redirect")) {
			return null;
		}

		String wUrl;
		if (rRequest.isHttpRequest() || (wUrl = getLoginWindowRedirectUrl(rRequest)) == null) {
			String lUrl = settings.getLoginPath(rRequest);
			if (!StringUtils.hasText(lUrl)) {
				lUrl = rPath + "/jsp/login_alert.jsp";
			}
			return rPath + "/jsp/login_redirect_template.jsp?login_redirect="
					+ rRequest.wrapContextPath(lUrl);
		} else {
			return wUrl;
		}
	}

	protected String getLoginWindowRedirectUrl(final PageRequestResponse rRequest) {
		return null;
	}

	@Override
	public IForward accessForward(final PageRequestResponse rRequest, final Object role) {
		final String roleName = getRole(role).getName();
		final String redirectUrl = getLoginRedirectUrl(rRequest, roleName);
		if (StringUtils.hasText(redirectUrl)) {
			return new UrlForward(redirectUrl);
		} else if (rRequest instanceof ComponentParameter && StringUtils.hasText(roleName)) {
			if (!rRequest.getLogin().isMember(role)) {
				return new UrlForward(MVCUtils.getPageResourcePath() + "/jsp/role_ajax_access.jsp?v="
						+ ((ComponentParameter) rRequest).getComponentName() + "&role=" + roleName);
			}
		}
		return null;
	}

	@Override
	public String getPhotoUrl(final PageRequestResponse rRequest, final Object user,
			final int width, final int height) {
		final StringBuilder sb = new StringBuilder();
		String path = MVCUtils.getPageResourcePath() + "/images";
		final PermissionUser pUser = user instanceof PermissionUser ? (PermissionUser) user
				: getUser(user);
		final InputStream inputStream = pUser.getPhotoStream();
		if (inputStream == null) {
			sb.append(path).append("/none_user.gif");
		} else {
			path += "/photo-cache/";
			final File photoCache = new File(MVCUtils.getRealPath(path));
			if (!photoCache.exists()) {
				FileUtils.createDirectoryRecursively(photoCache);
			}
			final String filename = pUser.getId() + "_" + width + "_" + height + ".png";
			final File photoFile = new File(photoCache.getAbsolutePath() + File.separator + filename);
			if (!photoFile.exists() || photoFile.length() == 0) {
				try {
					ImageUtils.thumbnail(inputStream, width, height, new FileOutputStream(photoFile));
				} catch (final IOException e) {
					log.warn(e);
				}
			}
			sb.append(path).append(filename);
		}
		return sb.toString();
	}

	@Override
	public String getLoginPhotoUrl(final PageRequestResponse rRequest, final int width,
			final int height) {
		return getPhotoUrl(rRequest, getLogin(rRequest), width, height);
	}

	@Override
	public String getPhotoUrl(final PageRequestResponse rRequest, final Object user) {
		return getPhotoUrl(rRequest, user, 128, 128);
	}

	@Override
	public String getLoginPhotoUrl(final PageRequestResponse rRequest) {
		return getPhotoUrl(rRequest, getLogin(rRequest));
	}

	@Override
	public ID getLoginId(final PageRequestResponse rRequest) {
		return null;
	}

	@Override
	public void login(final PageRequestResponse rRequest, final String login, final String password,
			final Map<String, Object> params) {
		throw NotImplementedException.of(getClass(), "login");
	}

	@Override
	public void login(final PageRequestResponse rRequest, final String login, final String password) {
		login(rRequest, login, password, null);
	}

	@Override
	public void logout(final PageRequestResponse rRequest) {
		throw NotImplementedException.of(getClass(), "logout");
	}
}
