package net.simpleframework.mvc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.simpleframework.common.Convert;
import net.simpleframework.common.FileUtils;
import net.simpleframework.common.ImageUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectEx;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.th.ThrowableUtils;
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
public class ImageCache extends ObjectEx implements IMVCSettingsAware {

	public static String NO_IMAGE_PATH = MVCUtils.getPageResourcePath() + "/images/no_image.jpg";

	private static Class<? extends ImageCache> _instanceClass;

	public static void setInstanceClass(final Class<? extends ImageCache> instanceClass) {
		_instanceClass = instanceClass;
	}

	public static ImageCache getInstance() {
		try {
			if (_instanceClass != null) {
				return _instanceClass.newInstance();
			}
		} catch (final Exception e) {
		}
		return new ImageCache();
	}

	private String _filename;

	private int width = 0;

	private int height = 0;

	private double scale;

	private boolean overwrite;

	private long lastModified;

	private String filetype;

	private String _load(final ImageStream iStream) {
		final String _id = Convert.toString(iStream.id);
		final File dir = mvcSettings.getHomeFile("/images/", _id);
		if (overwrite) {
			try {
				FileUtils.deleteAll(dir, true);
			} catch (final IOException e) {
			}
		}

		if (FileUtils.createDirectoryRecursively(dir)) {
			String filename = scale > 0 ? "" + scale : width + "_" + height;
			final String _type = StringUtils.hasText(filetype) ? filetype.toLowerCase() : "jpg";
			filename += "." + _type;

			final File oFile = new File(dir.getAbsolutePath() + File.separator + filename);
			synchronized (dir) {
				try {
					InputStream is;
					if ((!oFile.exists() || oFile.length() == 0)
							&& (is = iStream.getInputStream()) != null) {
						final FileOutputStream fos = new FileOutputStream(oFile);
						try {
							if (scale > 0) {
								ImageUtils.thumbnail(is, scale, fos, _type);
							} else {
								// if (width == 0 && height == 0) {
								// IoUtils.copyStream(is, fos);
								// } else {
								ImageUtils.thumbnail(is, width, height, fos, _type);
								// }
							}
						} finally {
							try {
								fos.close();
							} catch (final IOException e) {
							}
						}
					}
				} catch (final IOException e) {
					getLog().warn(e);
				} catch (final Exception e) {
					getLog().warn(ThrowableUtils.convertThrowable(e));
				}
			}
			if (oFile.exists() && oFile.length() > 0) {
				lastModified = oFile.lastModified();
				return _id + "/" + filename;
			}
		}
		return null;
	}

	public String getPath(final PageRequestResponse rRequest, final ImageStream iStream) {
		_filename = getImageLoadHandler().load(this, iStream);
		return getPath(rRequest);
	}

	public String getPath(final PageRequestResponse rRequest, final AttachmentFile aFile)
			throws IOException {
		final String durl = aFile.getDurl();
		if (StringUtils.hasText(durl)) {
			return durl;
		}
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
			final int p = oUrl.lastIndexOf("?");
			filetype = FileUtils.getFilenameExtension(p > 0 ? oUrl.substring(0, p) : oUrl);
			_filename = getImageLoadHandler().load(this, new ImageStream(ObjectUtils.hashStr(oUrl)) {

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
			path = rRequest.wrapContextPath(mvcSettings.wrapHomeUri("/images/" + _filename));
		} else {
			final String noImagePath = getNoImagePath();
			if (StringUtils.hasText(noImagePath)) {
				path = rRequest.wrapContextPath(noImagePath);
			} else {
				return null;
			}
		}
		if (lastModified > 0) {
			path = HttpUtils.addParameters(path, "t=" + lastModified);
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

	public String getNoImagePath() {
		return NO_IMAGE_PATH;
	}

	public static abstract class ImageStream {

		final Object id;

		public ImageStream(final Object id) {
			this.id = id;
		}

		protected abstract InputStream getInputStream() throws IOException;
	}

	public static interface IImageLoadHandler {

		String load(ImageCache iCache, ImageStream iStream);
	}

	private IImageLoadHandler _handler;

	public IImageLoadHandler getImageLoadHandler() {
		if (_handler == null) {
			_handler = new IImageLoadHandler() {
				@Override
				public String load(final ImageCache iCache, final ImageStream iStream) {
					return iCache._load(iStream);
				}
			};
		}
		return _handler;
	}

	public void setImageLoadHandler(final IImageLoadHandler _handler) {
		this._handler = _handler;
	}
}
