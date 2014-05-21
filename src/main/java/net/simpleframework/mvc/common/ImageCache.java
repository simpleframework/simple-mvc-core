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
	public static void setNoImagePath(final String path) {
		NO_IMAGE_PATH = path;
	}

	private static String NO_IMAGE_PATH = MVCUtils.getPageResourcePath() + "/images/no_image.jpg";

	private static final String CACHE_PATH = "/$image_cache/";
	static {
		FileUtils.createDirectoryRecursively(new File(MVCUtils.getRealPath(CACHE_PATH)));
	}

	private String _filename;

	public ImageCache(final String url) {
		this(url, 128, 128, false);
	}

	public ImageCache(final String oUrl, final int width, final int height, final boolean overwrite) {
		if (StringUtils.hasText(oUrl)) {
			_filename = load(ObjectUtils.hashStr(oUrl), width, height, overwrite, new IImageStream() {
				@Override
				public InputStream getInputStream() {
					InputStream is = null;
					try {
						if (HttpUtils.isAbsoluteUrl(oUrl)) {
							is = new URL(oUrl).openStream();
						}
					} catch (final IOException e) {
					}
					if (is == null) {
						try {
							is = new FileInputStream(new File(MVCUtils.getRealPath(oUrl)));
						} catch (final FileNotFoundException e) {
						}
					}
					return is;
				}
			});
		}
	}

	public ImageCache(final InputStream inputStream, final Object id, final int width,
			final int height, final boolean overwrite) {
		_filename = load(id, width, height, overwrite, new IImageStream() {
			@Override
			public InputStream getInputStream() {
				return inputStream;
			}
		});
	}

	public ImageCache(final InputStream inputStream, final Object id, final int width,
			final int height) {
		this(inputStream, id, width, height, false);
	}

	public ImageCache(final IAttachmentLobAware lob, final int width, final int height,
			final boolean overwrite) {
		this(lob.getAttachment(), lob.getMd(), width, height, overwrite);
	}

	public ImageCache(final IAttachmentLobAware lob, final int width, final int height) {
		this(lob, width, height, false);
	}

	private String load(final Object id, final int width, final int height, final boolean overwrite,
			final IImageStream imageLoad) {
		String filename = Convert.toString(id);
		if (width > 0) {
			filename += "_" + width;
		}
		if (height > 0) {
			filename += "_" + height;
		}
		filename += ".png";
		final File oFile = new File(MVCUtils.getRealPath(CACHE_PATH) + File.separator + filename);
		synchronized (CACHE_PATH) {
			if (overwrite || !oFile.exists() || oFile.length() == 0) {
				final InputStream is = imageLoad.getInputStream();
				if (is != null) {
					try {
						ImageUtils.thumbnail(is, width, height, new FileOutputStream(oFile), "png");
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

	public String getPath(final PageRequestResponse rRequest) {
		return getPath(rRequest, false);
	}

	public String getPath(final PageRequestResponse rRequest, final boolean timestamp) {
		String path;
		if (StringUtils.hasText(_filename)) {
			path = rRequest.wrapContextPath(CACHE_PATH + _filename);
		} else {
			if (StringUtils.hasText(NO_IMAGE_PATH)) {
				path = rRequest.wrapContextPath(NO_IMAGE_PATH);
			} else {
				return null;
			}
		}
		if (timestamp) {
			path = HttpUtils.addParameters(path, "t=" + System.currentTimeMillis());
		}
		return path;
	}

	interface IImageStream {

		InputStream getInputStream();
	}
}
