<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div align="center">
  <div class="ie_version">
    <p class="l1">#(ie_version_alert.0)</p>
    <p>
      #(ie_version_alert.1)&nbsp;<a href="javascript:go_browser();">#(ie_version_alert.2)&raquo;</a>
    </p>
    <p class="s">
      <a target="_blank" href="http://www.google.com/chrome/">Chrome</a>, <a target="_blank" href="http://firefox.com.cn/">Firefox</a>, <a
        target="_blank" href="http://www.opera.com/download/">Opera</a>#(ie_version_alert.9)<a target="_blank"
        href="http://windows.microsoft.com/zh-CN/internet-explorer/downloads/ie">IE9+</a>#(ie_version_alert.3)
    </p>
    <p class="s">
      #(ie_version_alert.4)<a target="_blank" href="http://www.google.com/chrome/">Chrome</a>#(ie_version_alert.5)<a target="_blank"
        href="http://chrome.360.cn/">#(ie_version_alert.6)</a>、<a target="_blank" href="http://ie.sogou.com/">#(ie_version_alert.7)</a>#(ie_version_alert.8)
    </p>
    <p>#(ie_version_alert.10)</p>
  </div>
</div>
<style type="text/css">
.ie_version {
  background: #fffff4;
  border: 9px solid #bbb;
  margin: 80px;
  padding: 20px;
  text-align: left;
  width: 720px;
}

.ie_version .s {
  border-top: 1px dashed #ccc;
  padding-top: 12px;
}

.ie_version p {
  font-size: 11.5pt;
}

.ie_version a {
  font-size: 16pt;
  font-weight: bold;
}
</style>
<script type="text/javascript">
  function go_browser() {
    document.setCookie("ie6_browser", "true", 24 * 365);
    $Actions.loc("/");
  }
</script>