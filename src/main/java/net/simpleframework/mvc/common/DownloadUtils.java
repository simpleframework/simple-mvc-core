package net.simpleframework.mvc.common;

import static net.simpleframework.common.I18n.$m;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.simpleframework.common.Convert;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.common.bean.AttachmentFile;
import net.simpleframework.mvc.MVCException;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DownloadUtils {

	public static String getDownloadHref(final AttachmentFile af) throws IOException {
		return getDownloadHref(af, null);
	}

	public static String getDownloadHref(final AttachmentFile af,
			final Class<? extends IDownloadHandler> handlerClass) {
		final StringBuilder sb = new StringBuilder();
		sb.append(MVCUtils.getPageResourcePath()).append("/jsp/download.jsp?filename=");
		sb.append(HttpUtils.encodeUrl(af.toFilename()));
		sb.append("&path=");
		try {
			final File attachment = af.getAttachment();
			if (attachment != null) {
				sb.append(StringUtils.encodeHex(attachment.getAbsolutePath().getBytes()));
			}
		} catch (final IOException e) {
			log.warn(e);
		}
		if (handlerClass != null) {
			sb.append("&id=").append(af.getId()).append("&handlerClass=")
					.append(handlerClass.getName());
		}
		return sb.toString();
	}

	public static final String DOWNLOAD_LOGIN = "download_login";

	public static void doDownload(final PageRequestResponse rRequest) throws IOException {
		if (Convert.toBool(rRequest.getParameter(DOWNLOAD_LOGIN), true) && !rRequest.isLogin()) {
			throw MVCException.of($m("DownloadUtils.0"));
		}
		final File oFile = new File(StringUtils.decodeHexString(rRequest.getParameter("path")));
		final OutputStream outputStream = rRequest.getBinaryOutputStream(
				HttpUtils.toLocaleString(rRequest.getParameter("filename")), oFile.length());

		final String[] headers = StringUtils.split(rRequest.getParameter("response-headers"), ";");
		if (headers != null) {
			for (final String s : headers) {
				final String[] kv = StringUtils.split(s, ":");
				if (kv.length == 2) {
					rRequest.setResponseHeader(kv[0], kv[1]);
				}
			}
		}

		final InputStream iStream = new FileInputStream(oFile);
		try {
			IoUtils.copyStream(iStream, outputStream);
			final String handlerClass = rRequest.getParameter("handlerClass");
			if (StringUtils.hasText(handlerClass)) {
				((IDownloadHandler) ObjectFactory.singleton(handlerClass)).onDownloaded(
						rRequest.getParameter("id"), rRequest.getParameter("filename"), oFile);
			}
		} finally {
			try {
				iStream.close();
			} catch (final IOException e) {
			}
		}
	}

	private static final Log log = LogFactory.getLogger(DownloadUtils.class);
}
