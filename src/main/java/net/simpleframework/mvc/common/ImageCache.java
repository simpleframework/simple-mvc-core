package net.simpleframework.mvc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.FileUtils;
import net.simpleframework.common.ImageUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.ctx.common.bean.AttachmentFile;
import net.simpleframework.mvc.IMVCContextVar;
import net.simpleframework.mvc.MVCUtils;
import net.simpleframework.mvc.PageRequestResponse;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class ImageCache extends ObjectEx implements IMVCContextVar {
	public static void setNoImagePath(final String path) {
		NO_IMAGE_PATH = path;
	}

	private static String NO_IMAGE_PATH = MVCUtils.getPageResourcePath() + "/images/no_image.jpg";

	private static final String CACHE_PATH = "/$image_cache/";
	static {
		FileUtils.createDirectoryRecursively(new File(MVCUtils.getRealPath(CACHE_PATH)));
	}

	private static IImageLoadHandler _handler;

	public static IImageLoadHandler getImageLoadHandler() {
		if (_handler == null) {
			_handler = new IImageLoadHandler() {
			};
		}
		return _handler;
	}

	public static void setImageLoadHandler(final IImageLoadHandler _handler) {
		ImageCache._handler = _handler;
	}

	private String _filename;

	private int width = 0;

	private int height = 0;

	private double scale;

	private boolean overwrite;

	private String filetype;

	private static Map<String, List<String>> cache = new HashMap<String, List<String>>();

	private String _load(final ImageStream iStream) {
		final String _id = Convert.toString(iStream.id);
		String filename = _id;
		if (scale > 0) {
			filename += "_" + scale;
		} else {
			if (width > 0) {
				filename += "_" + width;
			}
			if (height > 0) {
				filename += "_" + height;
			}
		}

		final boolean m = !filename.equals(_id);
		final String _type = StringUtils.hasText(filetype) ? filetype.toLowerCase() : "jpg";
		filename += "." + _type;
		if (m) {
			List<String> l = cache.get(_id);
			if (l == null) {
				cache.put(_id, l = new ArrayList<String>());
			}
			l.add(filename);
		}

		final String cpath = MVCUtils.getRealPath(CACHE_PATH);
		final File oFile = new File(cpath + File.separator + filename);
		if (overwrite) {
			oFile.delete();
			final List<String> l = cache.get(_id);
			if (l != null) {
				for (final String s : l) {
					new File(cpath + File.separator + s).delete();
				}
			}
		}

		synchronized (CACHE_PATH) {
			if (!oFile.exists() || oFile.length() == 0) {
				try {
					final InputStream is = iStream.getInputStream();
					if (is != null) {
						if (scale > 0) {
							ImageUtils.thumbnail(is, scale, new FileOutputStream(oFile), _type);
						} else {
							ImageUtils.thumbnail(is, width, height, new FileOutputStream(oFile), _type);
						}
						return filename;
					}
				} catch (final Exception e) {
					log.warn(mvcContext.getThrowableMessage(e));
				}
				return null;
			}
		}
		return filename;
	}

	public String getPath(final PageRequestResponse rRequest, final ImageStream iStream) {
		_filename = _load(iStream);
		return getPath(rRequest);
	}

	public String getPath(final PageRequestResponse rRequest, final AttachmentFile aFile)
			throws IOException {
		filetype = aFile.getExt();
		return getPath(rRequest, new ImageStream(aFile.getMd5()) {

			@Override
			public InputStream getInputStream() throws IOException {
				return new FileInputStream(aFile.getAttachment());
			}
		});
	}

	public String getPath(final PageRequestResponse rRequest, final String oUrl) {
		if (StringUtils.hasText(oUrl)) {
			filetype = FileUtils.getFilenameExtension(oUrl);
			_filename = _load(new ImageStream(ObjectUtils.hashStr(oUrl)) {

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
		return getPath(rRequest);
	}

	public String getPath(final PageRequestResponse rRequest) {
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
		if (overwrite) {
			path = HttpUtils.addParameters(path, "t=" + System.currentTimeMillis());
		}
		return path;
	}

	public ImageCache setWidth(final int width) {
		this.width = width;
		return this;
	}

	public ImageCache setHeight(final int height) {
		this.height = height;
		return this;
	}

	public ImageCache setOverwrite(final boolean overwrite) {
		this.overwrite = overwrite;
		return this;
	}

	public ImageCache setFiletype(final String filetype) {
		this.filetype = filetype;
		return this;
	}

	public ImageCache setScale(final double scale) {
		this.scale = scale;
		return this;
	}

	public static abstract class ImageStream {

		final Object id;

		public ImageStream(final Object id) {
			this.id = id;
		}

		protected abstract InputStream getInputStream() throws IOException;
	}

	public static interface IImageLoadHandler {

	}
}
