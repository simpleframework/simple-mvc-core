<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.common.AlgorithmUtils"%>
<%@ page import="net.simpleframework.common.I18n"%>
<%@ page import="net.simpleframework.common.StringUtils"%>
<%@ page import="net.simpleframework.mvc.PageRequestResponse"%>
<%
	String role = StringUtils.blank(request.getParameter("role"));
	String jname;
	if (role.startsWith("#")) {
		jname = I18n.$m("role_http_access.4");
		role = role.substring(1);
	} else {
		jname = I18n.$m("role_http_access.5");
	}
	final boolean http = PageRequestResponse.get(request, response)
			.isHttpRequest();
%>
<html>
<body>
  <div align="center">
    <div class="<%=http ? "job_http_access"
					: "job_http_access job_http_page_access"%>">
      <div class="simple_toolbar">
        <div class="error_image"></div>
        <div class="detail">
          <div class="t1">#(role_http_access.0)</div>
          <div class="t2 wrap_text"><%=new String(AlgorithmUtils.base64Decode(request
					.getParameter("v")))%></div>
          <div class="t3"><%=I18n.$m("role_http_access.1", jname, role)%></div>
        </div>
      </div>
      <div class="bbar">
        <%
        	if (http) {
        %>
        <input type="button" value="#(role_http_access.2)" onclick="history.back();" /> <input
          type="button" value="#(role_http_access.3)" onclick="$Actions.loc('/');" />
        <%
        	} else {
        %>
        <input type="button" value="#(Button.Close)" onclick="$win(this).close();" />
        <%
        	}
        %>
      </div>
    </div>
  </div>
</body>
</html>
