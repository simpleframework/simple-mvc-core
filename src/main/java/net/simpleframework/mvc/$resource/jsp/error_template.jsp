<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.common.web.HttpUtils"%>
<%@ page import="net.simpleframework.common.StringUtils"%>
<%
	String url = (String) session.getAttribute("systemErrorPage");
	session.removeAttribute("systemErrorPage");
	if (!StringUtils.hasText(url)) {
		return;
	}
%>
<script type="text/javascript">
	(window.parent || window).location = "<%=HttpUtils.wrapContextPath(request, url)%>";
</script>