<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.common.StringUtils"%>
<%
	String url = (String) session.getAttribute("login_redirect");
	session.removeAttribute("login_redirect");
	if (!StringUtils.hasText(url)) {
    url = "/";
	}
%>
<script type="text/javascript">
	(window.parent || window).location = "<%=url%>";
</script>