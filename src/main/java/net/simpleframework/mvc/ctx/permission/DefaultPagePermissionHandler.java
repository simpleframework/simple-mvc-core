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
import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.IMVCSettingsAware;
import net.simpleframework.mvc.MVCConst;
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
public class DefaultPagePermissionHandler extends DefaultPermissionHandler
		implements IPagePermissionHandler, IMVCSettingsAware {
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
		if (!StringUtils.hasText(role) || PermissionConst.ROLE_ANONYMOUS.equals(role)) {
			return null;
		}

		final String rPath = MVCUtils.getPageResourcePath();
		if (rRequest.getRequestURI().contains(rPath + "/jsp/login_redirect")) {
			return null;
		}

		String wUrl;
		if (rRequest.isHttpRequest() || (wUrl = getLoginWindowRedirectUrl(rRequest)) == null) {
			String lUrl = mvcSettings.getLoginPath(rRequest);
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
		final String rolename = rRequest.getRole(role).getName();
		final String redirectUrl = getLoginRedirectUrl(rRequest, rolename);
		if (StringUtils.hasText(redirectUrl)) {
			return new UrlForward(redirectUrl);
		} else if (rRequest instanceof ComponentParameter && StringUtils.hasText(rolename)) {
			if (!rRequest.isLmember(role)) {
				return new UrlForward(MVCUtils.getPageResourcePath() + "/jsp/role_ajax_access.jsp?v="
						+ ((ComponentParameter) rRequest).getComponentName() + "&role=" + rolename);
			}
		}
		return null;
	}

	@Override
	public String getPhotoUrl(final PageRequestResponse rRequest, final Object user, final int width,
			final int height) {
		final StringBuilder sb = new StringBuilder();
		final PermissionUser pUser = user instanceof PermissionUser ? (PermissionUser) user
				: getUser(user);
		final Object id = pUser.getId();
		final File photoCache = mvcSettings.getHomeFile("/images/", String.valueOf(id));
		if (!photoCache.exists()) {
			FileUtils.createDirectoryRecursively(photoCache);
		}

		final String filename = width + "_" + height + ".png";
		final File photoFile = new File(photoCache.getAbsolutePath() + File.separator + filename);

		if (!photoFile.exists() || photoFile.length() == 0) {
			final InputStream inputStream = pUser.getPhotoStream();
			if (inputStream == null) {
				sb.append(MVCUtils.getPageResourcePath()).append("/images/none_user.gif");
				return sb.toString();
			} else {
				try {
					ImageUtils.thumbnail(inputStream, width, height, new FileOutputStream(photoFile));
				} catch (final IOException e) {
					getLog().warn(e);
				}
			}
		}
		sb.append(MVCConst.IMAGES_PATH).append("/").append(id).append("/").append(filename)
				.append("?last=").append(photoFile.lastModified());
		return sb.toString();
	}

	@Override
	public void clearPhotoCache(final PageRequestResponse rRequest, final Object user)
			throws IOException {
		final PermissionUser pUser = user instanceof PermissionUser ? (PermissionUser) user
				: getUser(user);
		FileUtils.deleteAll(mvcSettings.getHomeFile("/images/" + pUser.getId() + "/"));
	}

	@Override
	public String getPhotoUrl(final PageRequestResponse rRequest, final Object user) {
		return getPhotoUrl(rRequest, user, 160, 160);
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
	public void login(final PageRequestResponse rRequest, final String login,
			final String password) {
		login(rRequest, login, password, null);
	}

	@Override
	public void logout(final PageRequestResponse rRequest) {
		throw NotImplementedException.of(getClass(), "logout");
	}
}
