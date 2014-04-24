package net.simpleframework.mvc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.simpleframework.ado.bean.IAttachmentLobAware;
import net.simpleframework.common.Convert;
import net.simpleframework.common.FileUtils;
import net.simpleframework.common.ImageUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ImageCache extends ObjectEx {
	private static final String NO_IMAGE_PATH = MVCUtils.getPageResourcePath()
			+ "/images/no_image.jpg";

	private static final String CACHE_PATH = "/$image_cache/";
	static {
		FileUtils.createDirectoryRecursively(new File(MVCUtils.getRealPath(CACHE_PATH)));
	}

	private String filename;

	public ImageCache(final String url) {
		this(url, 128, 128);
	}

	public ImageCache(final String oUrl, final int width, final int height) {
		if (StringUtils.hasText(oUrl)) {
			filename = load(ObjectUtils.hashStr(oUrl), width, height, new IImageStream() {
				@Override
				public InputStream getInputStream() {
					InputStream inputStream = null;
					try {
						if (HttpUtils.isAbsoluteUrl(oUrl)) {
							inputStream = new URL(oUrl).openStream();
						}
					} catch (final IOException e) {
					}
					if (inputStream == null) {
						try {
							inputStream = new FileInputStream(new File(MVCUtils.getRealPath(oUrl)));
						} catch (final FileNotFoundException e) {
						}
					}
					return inputStream;
				}
			});
		}
	}

	public ImageCache(final InputStream inputStream, final Object id, final int width,
			final int height) {
		filename = load(id, width, height, new IImageStream() {
			@Override
			public InputStream getInputStream() {
				return inputStream;
			}
		});
	}

	public ImageCache(final IAttachmentLobAware lob, final int width, final int height) {
		this(lob.getAttachment(), lob.getMd(), width, height);
	}

	private String load(final Object imageId, final int width, final int height,
			final IImageStream imageLoad) {
		String filename = Convert.toString(imageId);
		if (width > 0) {
			filename += "_" + width;
		}
		if (height > 0) {
			filename += "_" + height;
		}
		filename += ".png";
		final File cFile = new File(MVCUtils.getRealPath(CACHE_PATH) + File.separator + filename);
		synchronized (CACHE_PATH) {
			if (!cFile.exists() || cFile.length() == 0) {
				final InputStream inputStream = imageLoad.getInputStream();
				if (inputStream != null) {
					try {
						ImageUtils.thumbnail(inputStream, width, height, new FileOutputStream(cFile),
								"png");
					} catch (final IOException e) {
						log.warn(e);
					}
				} else {
					return null;
				}
			}
		}
		return filename;
	}

	public String getPath() {
		return StringUtils.hasText(filename) ? CACHE_PATH + filename : NO_IMAGE_PATH;
	}

	public String getPath(final PageRequestResponse rRequest) {
		return rRequest.wrapContextPath(getPath());
	}

	interface IImageStream {

		InputStream getInputStream();
	}
}
