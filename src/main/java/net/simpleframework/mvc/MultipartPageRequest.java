package net.simpleframework.mvc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.simpleframework.common.FileUtils;
import net.simpleframework.common.IoUtils;
import net.simpleframework.lib.com.oreilly.servlet.MultipartRequest;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MultipartPageRequest extends HttpServletRequestWrapper implements
		IMultipartPageRequest, IMVCSettingsAware {
	private final MultipartRequest mRequest;

	public MultipartPageRequest(final HttpServletRequest request, final int maxUploadSize)
			throws IOException {
		super(request);
		final File dir = new File(mvcSettings.getTmpFiledir().getAbsolutePath() + File.separator
				+ "uploads" + File.separator);
		if (!dir.exists()) {
			FileUtils.createDirectoryRecursively(dir);
		}
		mRequest = new MultipartRequest(request, dir.getAbsolutePath(), maxUploadSize,
				mvcSettings.getCharset());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getParameterNames() {
		return (Enumeration<String>) mRequest.getParameterNames();
	}

	@Override
	public String[] getParameterValues(final String name) {
		return mRequest.getParameterValues(name);
	}

	@Override
	public String getParameter(final String name) {
		return mRequest.getParameter(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getFileNames() {
		return (Enumeration<String>) mRequest.getFileNames();
	}

	@Override
	public IMultipartFile getFile(final String name) {
		return new IMultipartFile() {

			@Override
			public String getOriginalFilename() {
				return mRequest.getOriginalFileName(name);
			}

			@Override
			public File getFile() {
				return mRequest.getFile(name);
			}

			@Override
			public long getSize() {
				final File file = mRequest.getFile(name);
				return file == null ? 0 : file.length();
			}

			@Override
			public InputStream getInputStream() throws IOException {
				final File file = mRequest.getFile(name);
				return file == null ? null : new FileInputStream(file);
			}

			@Override
			public byte[] getBytes() throws IOException {
				final ByteArrayOutputStream oStream = new ByteArrayOutputStream();
				IoUtils.copyStream(getInputStream(), oStream);
				return oStream.toByteArray();
			}

			@Override
			public void transferTo(final File file) throws IOException {
				if (file.exists() && !file.delete()) {
					throw new IOException("Destination file [" + file.getAbsolutePath()
							+ "] already exists and could not be deleted");
				}
				try {
					FileUtils.copyFile(getInputStream(), file);
				} catch (final IOException ex) {
					throw ex;
				} catch (final Exception ex) {
					throw new IOException("Could not transfer to file: " + ex.getMessage());
				}
			}
		};
	}
}
