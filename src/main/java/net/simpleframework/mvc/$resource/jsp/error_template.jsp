<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.common.web.HttpUtils"%>
<script type="text/javascript">
  (window.parent || window).location = 
    "<%=HttpUtils.wrapContextPath(request, request.getParameter("systemErrorPage"))%>";
</script>