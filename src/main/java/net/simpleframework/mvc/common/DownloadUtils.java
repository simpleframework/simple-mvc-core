package net.simpleframework.mvc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import net.simpleframework.common.IoUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.common.bean.AttachmentFile;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class DownloadUtils {

	public static String getDownloadHref(final AttachmentFile af) {
		return getDownloadHref(af, null);
	}

	public static String getDownloadHref(final AttachmentFile af,
			final Class<? extends IDownloadHandler> handlerClass) {
		final StringBuilder sb = new StringBuilder();
		sb.append(MVCUtils.getPageResourcePath()).append("/jsp/download.jsp?filename=");
		String topic = af.getTopic();
		final String type = af.getType();
		if (type != null && !topic.endsWith(type)) {
			topic += "." + type;
		}
		sb.append(HttpUtils.encodeUrl(topic));
		sb.append("&path=");
		final File attachment = af.getAttachment();
		if (attachment != null) {
			sb.append(StringUtils.encodeHex(attachment.getAbsolutePath().getBytes()));
		}
		if (handlerClass != null) {
			sb.append("&id=").append(af.getId()).append("&handlerClass=")
					.append(handlerClass.getName());
		}
		return sb.toString();
	}

	public static void doDownload(final PageRequestResponse rRequest) throws IOException {
		final File oFile = new File(StringUtils.decodeHexString(rRequest.getParameter("path")));
		final OutputStream outputStream = rRequest.getBinaryOutputStream(
				HttpUtils.toLocaleString(rRequest.getParameter("filename")), oFile.length());
		IoUtils.copyStream(new FileInputStream(oFile), outputStream);

		final String handlerClass = rRequest.getParameter("handlerClass");
		if (StringUtils.hasText(handlerClass)) {
			((IDownloadHandler) ObjectFactory.singleton(handlerClass)).onDownloaded(
					rRequest.getParameter("id"), rRequest.getParameter("filename"), oFile);
		}
	}
}
