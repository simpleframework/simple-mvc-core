package net.simpleframework.mvc.ctx.permission;

import java.io.IOException;

import javax.servlet.FilterChain;

import net.simpleframework.common.AlgorithmUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.permission.LoginUser;
import net.simpleframework.ctx.permission.LoginUser.LoginWrapper;
import net.simpleframework.mvc.IFilterListener;
import net.simpleframework.mvc.IMVCContextVar;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PermissionFilterListener implements IFilterListener, IMVCContextVar {

	@Override
	public EFilterResult doFilter(final PageRequestResponse rRequest, final FilterChain filterChain)
			throws IOException {
		final IPagePermissionHandler permission = ctx.getPermission();

		if (rRequest.isHttpRequest()) {
			// 获取页面角色
			String role = null;
			if (rRequest instanceof PageParameter) {
				role = (String) ((PageParameter) rRequest).getBeanProperty("role");
				// if (rRequest.isAjaxRequest() && !StringUtils.hasText(role)) {
				// final PageDocument pageDocument2 =
				// PageDocumentFactory.getPageDocument(rRequest
				// .getParameter(IMVCConst.PARAM_DOCUMENT));
				// if (pageDocument2 != null) {
				// final PageParameter pp2 = PageParameter.get(rRequest,
				// pageDocument2);
				// role = (String) pp2.getBeanProperty("role");
				// }
				// }
			}

			LoginUser.set(null);

			// 登录判断
			String loginUrl;
			if (StringUtils.hasText(loginUrl = permission.getLoginRedirectUrl(rRequest, role))) {
				rRequest.loc(loginUrl);
				return EFilterResult.BREAK;
			}

			// 权限判断
			if (StringUtils.hasText(role) && !IPermissionConst.ROLE_ANONYMOUS.equals(role)) {
				if (!permission.getLogin(rRequest).isMember(role)) {
					final String v = AlgorithmUtils.base64Encode(rRequest.getRequestAndQueryStringUrl()
							.getBytes());
					rRequest.loc(MVCUtils.getPageResourcePath() + "/jsp/role_http_access.jsp?v=" + v
							+ "&role=" + role);
					return EFilterResult.BREAK;
				}
			}
		}

		// 设置当前登录用户到线程对象
		LoginUser
				.set(new LoginWrapper(permission.getLogin(rRequest)).setIp(rRequest.getRemoteAddr()));
		return EFilterResult.SUCCESS;
	}
}
