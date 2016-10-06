<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.MVCContext"%>
<%@ page import="net.simpleframework.mvc.PageRequestResponse"%>
<%@ page import="net.simpleframework.mvc.common.DownloadUtils"%>
<%
	try {
		DownloadUtils.doDownload(PageRequestResponse.get(request, response));
		out.clear();
		out.clearBuffer();
		out = pageContext.pushBody();
	} catch (Throwable th) {
		out.write("<div style='text-align: center; font-size: 15pt; padding: 10%;'>");
		out.write(MVCContext.get().getThrowableMessage(th));
		out.write("</div>");
	}
%>