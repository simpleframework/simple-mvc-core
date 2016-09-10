package net.simpleframework.mvc;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCConst {
	public static final String JSESSIONID = "jsessionid";

	public static final String REQUEST_ID = "@rid";

	/* request parameter */
	public static final String PARAM_XMLPATH = "_xmlpath";
	public static final String PARAM_REFERER = "_referer";
	public static final String PARAM_PARENT_PAGE = "_parent";

	/* request attribute */

	/* session attribute */
	public static final String SESSION_ATTRI_COOKIES = "_cookies";
	public static final String SESSION_ATTRI_THROWABLE = "_throwable";
	public static final String SESSION_ATTRI_LASTURL = "_lasturl";
	public static final String SESSION_ATTRI_SKIN = "_skin";

	public static final String PAGELOAD_TIME = "pageload_time";

	/* html */
	public static final String HTML_BASE64_CLASS = "html_base64_class";

	/* images cache */
	public static final String IMAGES_CACHE_PATH = "/$image_cache/";
}
