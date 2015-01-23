<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.common.I18n"%>
<%@ page import="net.simpleframework.common.StringUtils"%>
<%
	String role = StringUtils.blank(request.getParameter("role"));
	String jname;
	if (role.startsWith("#")) {
		jname = I18n.$m("role_http_access.4");
		role = role.substring(1);
	} else {
		jname = I18n.$m("role_http_access.5");
	}
%>
<div class="job_ajax_access">
  <div class="cbar">
    <div class="error_image"></div>
    <div class="detail">
      <div class="t1"><%=I18n.$m("role_ajax_access.0")%>
        <span class="s1"><%=I18n.$m("role_ajax_access.2", request.getParameter("v"))%></span>
      </div>
      <div class="t2"><%=I18n.$m("role_http_access.1", jname, role)%></div>
    </div>
  </div>
  <div class="bbar">
    <input type="button" value="<%=I18n.$m("Button.Close")%>" onclick="$Actions['jobAccessWindow'].close();" />
  </div>
</div>
