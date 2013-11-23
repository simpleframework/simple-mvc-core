package net.simpleframework.mvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import net.simpleframework.common.TextUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageResponse extends HttpServletResponseWrapper implements IMVCContextVar, IMVCConst {
	private class BufferedServletOutputStream extends ServletOutputStream {
		private OutputStream os;

		private ByteArrayOutputStream bos; // for html string

		public BufferedServletOutputStream() throws IOException {
			super();
			bos = new ByteArrayOutputStream();
		}

		@Override
		public void close() throws IOException {
			if (os != null) {
				os.close();
				os = null;
			}
			if (bos != null) {
				bos.close();
				bos = null;
			}
		}

		@Override
		public void write(final int b) throws IOException {
			if (os != null) {
				os.write(b);
			}
			if (bos != null) {
				bos.write(b);
			}
		}

		public void createOutputStream() throws IOException {
			os = getResponse().getOutputStream();
			if (gzipContent) {
				os = new GZIPOutputStream(os);
			}
			if (bos != null) {
				bos.close();
				bos = null;
			}
		}
	}

	private final boolean bHttpRequest;

	public PageResponse(final HttpServletResponse response, final boolean bHttpRequest)
			throws IOException {
		super(response);
		this.bHttpRequest = bHttpRequest;
	}

	private boolean gzipContent;

	public void setGzipContentEncoding() {
		setHeader("Content-Encoding", "gzip");
		gzipContent = true;
	}

	void initOutputStream() throws IOException {
		if (stream != null) {
			stream.createOutputStream();
		}
	}

	@Override
	public void addCookie(final Cookie cookie) {
		if (bHttpRequest) {
			super.addCookie(cookie);
		} else {
			// 加入 session
			@SuppressWarnings("unchecked")
			List<Cookie> cookies = (List<Cookie>) SessionCache.lget(SESSION_ATTRI_COOKIES);
			if (cookies == null) {
				SessionCache.lput(SESSION_ATTRI_COOKIES, cookies = new ArrayList<Cookie>());
			}
			cookies.add(cookie);
		}
	}

	@Override
	public void flushBuffer() throws IOException {
		// 避免数据输出到客户端
	}

	private BufferedServletOutputStream stream;

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (stream == null) {
			stream = new BufferedServletOutputStream();
		}
		return stream;
	}

	private PrintWriter writer;

	private Writer output;

	@Override
	public PrintWriter getWriter() {
		if (writer == null) {
			// 貌似StringBuilder比CharArrayWriter要快
			// 我写了个测试程序，至少是CharArrayWriter的一半时间
			writer = new PrintWriter(output = new Writer() {
				final StringBuilder buffer = new StringBuilder(1024);

				@Override
				public void write(final char[] cbuf, final int off, final int len) throws IOException {
					buffer.append(cbuf, off, len);
				}

				@Override
				public void write(final String str, final int off, final int len) throws IOException {
					buffer.append(str, off, len);
				}

				@Override
				public void flush() throws IOException {
				}

				@Override
				public void close() throws IOException {
				}

				@Override
				public String toString() {
					return buffer.toString();
				}
			});
		}
		return writer;
	}

	@Override
	public String toString() {
		if (writer != null) {
			// tomcat, jetty ...
			return output.toString();
		} else if (stream != null && stream.bos != null) {
			// weblogic ...
			try {
				final byte[] bytes = stream.bos.toByteArray();
				return new String(bytes, TextUtils.isUTF8(bytes) ? "UTF-8" : settings.getCharset());
			} catch (final UnsupportedEncodingException e) {
			}
		}
		return super.toString();
	}
}
