package net.simpleframework.mvc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.simpleframework.common.Base64;
import net.simpleframework.common.Convert;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.common.bean.AttachmentFile;
import net.simpleframework.mvc.IMVCSettingsAware;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DownloadUtils implements IMVCSettingsAware {

	public static String getDownloadHref(final AttachmentFile af) {
		return getDownloadHref(af, null);
	}

	public static String getDownloadHref(final AttachmentFile af,
			final Class<? extends IDownloadHandler> handlerClass) {
		return getDownloadHref(af, null, handlerClass);
	}

	public static String getDownloadHref(final AttachmentFile af, final Boolean anonymous,
			final Class<? extends IDownloadHandler> handlerClass) {
		return getDownloadHref(af, af.getDurl(), anonymous, handlerClass);
	}

	public static String getDownloadHref(final AttachmentFile af, final String durl,
			final Boolean anonymous, final Class<? extends IDownloadHandler> handlerClass) {
		final StringBuilder sb = new StringBuilder();
		sb.append(MVCUtils.getPageResourcePath()).append("/jsp/download.jsp?");
		if (StringUtils.hasText(durl)) {
			sb.append("durl=").append(HttpUtils.encodeUrl(durl));
		} else {
			sb.append("path=");
			try {
				final File attachment = af.getAttachment();
				if (attachment != null) {
					sb.append(StringUtils.encodeHex(attachment.getAbsolutePath().getBytes()));
				}
			} catch (final IOException e) {
				log.warn(e);
			}
		}

		sb.append("&filename=").append(HttpUtils.encodeUrl(af.toFilename()));
		sb.append("&size=").append(af.getSize());
		if (Convert.toBool(af.getAttr("pdf"))) {
			sb.append("&pdf=true");
		}
		sb.append("&filetype=").append(af.getExt());

		if (anonymous != null) {
			sb.append("&_login=").append(!anonymous);
		}
		if (handlerClass != null) {
			sb.append("&id=").append(af.getId()).append("&handlerClass=")
					.append(handlerClass.getName());
		}
		return sb.toString();
	}

	public static void doDownload(final PageRequestResponse rRequest) throws IOException {
		if (!rRequest.isLogin()) {
			// 未登录
			boolean anonymous;
			final String _login = rRequest.getParameter("_login");
			if (StringUtils.hasText(_login)) {
				anonymous = !Convert.toBool(_login);
			} else {
				anonymous = mvcSettings.isAnonymousDownload(rRequest);
			}
			if (!anonymous) {
				rRequest.loc(mvcSettings.getLoginPath(rRequest));
				return;
				// throw MVCException.of($m("DownloadUtils.0"));
			}
		}

		final String durl = rRequest.getParameter("durl");
		if (StringUtils.hasText(durl)) {
			if (rRequest.getBoolParameter("pdf")) {
				String filename = rRequest.getParameter("filename");
				final int p = filename.lastIndexOf(".");
				if (p > 0) {
					filename = filename.substring(0, p);
				}
				rRequest.loc("/pdf/viewer?topic=" + HttpUtils.encodeUrl(filename) + "&file="
						+ HttpUtils.encodeUrl(Base64.encodeToString(durl)));
			} else {
				rRequest.loc(durl);
			}
		} else {
			final OutputStream outputStream = rRequest.getBinaryOutputStream(
					HttpUtils.toLocaleString(rRequest.getParameter("filename")),
					rRequest.getIntParameter("size"), rRequest.getBoolParameter("inline"));

			final String[] headers = StringUtils.split(rRequest.getParameter("response-headers"), ";");
			if (headers != null) {
				for (final String s : headers) {
					final String[] kv = StringUtils.split(s, ":");
					if (kv.length == 2) {
						rRequest.setResponseHeader(kv[0], kv[1]);
					}
				}
			}

			final File oFile = new File(StringUtils.decodeHexString(rRequest.getParameter("path")));
			final InputStream iStream = new FileInputStream(oFile);
			try {
				IoUtils.copyStream(iStream, outputStream);
			} finally {
				try {
					iStream.close();
				} catch (final IOException e) {
				}
			}
		}

		// 触发事件
		final String handlerClass = rRequest.getParameter("handlerClass");
		if (StringUtils.hasText(handlerClass)) {
			String topic = durl;
			if (!StringUtils.hasText(topic)) {
				topic = rRequest.getLocaleParameter("filename");
			}
			((IDownloadHandler) ObjectFactory.singleton(handlerClass)).onDownloaded(
					rRequest.getParameter("id"), rRequest.getIntParameter("size"),
					rRequest.getParameter("filetype"), topic);
		}
	}

	private static final Log log = LogFactory.getLogger(DownloadUtils.class);
}
