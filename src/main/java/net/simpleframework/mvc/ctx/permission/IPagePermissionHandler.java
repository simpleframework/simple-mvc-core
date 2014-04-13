package net.simpleframework.mvc.ctx.permission;

import java.util.Map;

import net.simpleframework.common.ID;
import net.simpleframework.ctx.permission.IPermissionHandler;
import net.simpleframework.ctx.permission.PermissionUser;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IPagePermissionHandler extends IPermissionHandler {

	/**
	 * 提供组件的访问权限，权限系统实现
	 * 
	 * @param compParameter
	 * @param role
	 *           角色名
	 * @return
	 */
	IForward accessForward(PageRequestResponse rRequest, Object role);

	/**
	 * 获取当前的登录用户
	 * 
	 * @param rRequest
	 * @return
	 */
	ID getLoginId(PageRequestResponse rRequest);

	/**
	 * 获取当前的登录用户的权限
	 * 
	 * @param rRequest
	 * @return
	 */
	PermissionUser getLogin(PageRequestResponse rRequest);

	/**
	 * 登陆验证
	 * 
	 * @param rRequest
	 * @param login
	 * @param password
	 * @param params
	 */
	void login(PageRequestResponse rRequest, String login, String password,
			Map<String, Object> params);

	void login(PageRequestResponse rRequest, String login, String password);

	/**
	 * 注销
	 * 
	 * @param rRequest
	 */
	void logout(PageRequestResponse rRequest);

	/**
	 * 获取登录验证的转向地址，返回null表示不需要转向
	 * 
	 * @param requestResponse
	 * @param role
	 * @return
	 */
	String getLoginRedirectUrl(PageRequestResponse rRequest, String role);

	/**
	 * 获取用户头像的地址
	 * 
	 * @param rRequest
	 * @param user
	 * @param width
	 * @param height
	 * @return
	 */
	String getPhotoUrl(PageRequestResponse rRequest, Object user, int width, int height);

	String getLoginPhotoUrl(PageRequestResponse rRequest, int width, int height);

	/**
	 * 默认128*128
	 * 
	 * @param rRequest
	 * @param user
	 * @return
	 */
	String getPhotoUrl(PageRequestResponse rRequest, Object user);

	String getLoginPhotoUrl(PageRequestResponse rRequest);
}
