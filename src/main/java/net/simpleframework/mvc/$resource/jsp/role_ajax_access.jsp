<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.UUID"%>

<%
	final String id = "ID_" + UUID.randomUUID();
%>
<div id="<%=id%>"></div>
<script type="text/javascript">
  (function() {
    var win = $win("<%=id%>");
    if (win) {
      win.observe("hidden", function() { 
        $Actions["jobAccessWindow"]("<%=request.getQueryString()%>");
      });
      new PeriodicalExecuter(function(executer) {
        if (win.visible) {
          win.close();
          executer.stop();
        }
      }, 0.1);
    } else {
      (function() {
        var ele = $("<%=id%>");
        if (ele) {
          ele.remove();
        }
        $Actions["jobAccessWindow"]("<%=request.getQueryString()%>");
      }).delay(0.5);
    }
  })();
</script>