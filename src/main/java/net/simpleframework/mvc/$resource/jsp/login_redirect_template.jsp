<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.common.StringUtils"%>
<%@ page import="net.simpleframework.common.I18n"%>
<%@ page import="net.simpleframework.mvc.PageRequestResponse"%>
<%@ page import="net.simpleframework.mvc.MVCUtils"%>
<%
	String url = (String) session.getAttribute("login_redirect");
	session.removeAttribute("login_redirect");
	if (!StringUtils.hasText(url)) {
		url = MVCUtils.getLoginUrl(new PageRequestResponse(request, response));
	}
%>
<html>
<head>
<title><%=I18n.$m("location.jsp.0")%></title>
<script type="text/javascript">
	window.location = "<%=url%>";
</script>
</head>
</html>