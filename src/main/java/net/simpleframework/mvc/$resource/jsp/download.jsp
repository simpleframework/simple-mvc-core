<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="net.simpleframework.mvc.common.DownloadUtils"%>
<%@ page import="net.simpleframework.mvc.PageRequestResponse"%>
<%@ page import="net.simpleframework.mvc.IMVCContextVar"%>
<%
  try {
    DownloadUtils.doDownload(PageRequestResponse.get(request,
        response));
  } catch (Throwable th) {
    System.out.println(IMVCContextVar.ctx.getThrowableMessage(th));
  } finally {
    try {
      out.clear();
      out.clearBuffer();
      out = pageContext.pushBody();
    } catch (Throwable th) {
    }
  }
%>