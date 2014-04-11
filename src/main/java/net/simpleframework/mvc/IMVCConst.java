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

	/* request parameter */
	final static String PARAM_XMLPATH = "_xmlpath";
	final static String PARAM_REFERER = "_referer";
	final static String PARAM_PARENT_PAGE = "_parent";

	/* request attribute */

	/* session attribute */
	final static String SESSION_ATTRI_COOKIES = "_cookies";
	final static String SESSION_ATTRI_THROWABLE = "_throwable";
	final static String SESSION_ATTRI_LASTURL = "_lasturl";
	final static String SESSION_ATTRI_SKIN = "_skin";

	final static String PAGELOAD_TIME = "pageload_time";

	/* html */
	static String HTML_BASE64_CLASS = "html_base64_class";
}
