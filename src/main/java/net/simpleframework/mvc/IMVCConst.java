package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IMVCConst {

	static final String JSESSIONID = "jsessionid";

	static final String REQUEST_ID = "@rid";

	static final String KEEP_REQUESTDATA_CACHE = "keep_requestdata_cache";

	/* request parameter */
	final static String PARAM_XMLPATH = "_xmlpath";
	final static String PARAM_REFERER = "_referer";
	final static String PARAM_DOCUMENT = "_document";

	final static String PARAM_AJAX_REQUEST_MARK = "_ajax_request_mark";

	/* session attribute */
	final static String SESSION_ATTRI_COOKIES = "_cookies";
	final static String SESSION_ATTRI_THROWABLE = "_throwable";
	final static String SESSION_ATTRI_LASTURL = "_lasturl";
	final static String SESSION_ATTRI_SKIN = "_skin";

	final static String SESSION_ATTRI_PAGE_TITLE = "_page_title";

	/* cookie */
	final static String COOKIE_PAGELOAD_TIME = "pageload_time";

	/* html */
	static String HTML_BASE64_CLASS = "html_base64_class";

	/* doc tag */
	final static String TAG_HANDLE_CLASS = "handleClass";
	final static String TAG_COMPONENTS = "components";
	final static String TAG_SCRIPT_INIT = "scriptInit";
	final static String TAG_SCRIPT_EVAL = "scriptEval";
	final static String TAG_IMPORT_PAGE = "importPage";
	final static String TAG_IMPORT_JAVASCRIPT = "importJavascript";
	final static String TAG_IMPORT_CSS = "importCSS";
	final static String TAG_VALUE = "value";
}
