package net.simpleframework.mvc;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.common.web.HttpUtils;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class PageDocumentFactory implements IMVCContextVar, IMVCConst {

	/**
	 * 缓存查询路径下的类
	 */
	private static Map<String, PageWrapper> _wrappers;
	static {
		_wrappers = new ConcurrentHashMap<String, PageWrapper>();
	}

	static void addPageClass(final String url, final Class<? extends AbstractMVCPage> pageClass,
			final int priority) {
		final PageWrapper wrapper = _wrappers.get(url);
		if (wrapper == null || wrapper.priority < priority) {
			_wrappers.put(url, new PageWrapper(pageClass, priority));
		}
	}

	static PageDocument getPageDocument(final PageRequestResponse rRequest) {
		String xmlpath = rRequest.getParameter(PARAM_XMLPATH);
		if (StringUtils.hasText(xmlpath)) {
			return getPageDocumentAndCreate(new File(MVCUtils.getRealPath(xmlpath)), rRequest);
		} else {
			String lookupPath = rRequest.stripContextPath(HttpUtils.getRequestURI(rRequest.request,
					false));
			int pos;
			if ((pos = lookupPath.indexOf(";")) > 0) {
				lookupPath = lookupPath.substring(0, pos); // strip jsessionid
			}

			final PageDocument pageDocument = getPageDocument(rRequest, lookupPath);
			if (pageDocument != null) {
				return pageDocument;
			} else {
				final int p = lookupPath.lastIndexOf('.');
				xmlpath = MVCUtils.getRealPath(((p <= 0) ? lookupPath : lookupPath.substring(0, p))
						+ ".xml");
				return getPageDocumentAndCreate(new File(xmlpath), rRequest);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static PageDocument getPageDocument(final PageRequestResponse rRequest,
			final String lookupPath) {
		final String filterPath = settings.getFilterPath();
		PageDocument pageDocument = null;
		if (StringUtils.hasText(filterPath) && lookupPath.startsWith(filterPath)) {
			PageWrapper wrapper = _wrappers.get(lookupPath);
			l: if (wrapper == null) {
				boolean homeMark = false;
				String clazzName = lookupPath.substring(filterPath.length());
				if (!StringUtils.hasText(clazzName) || "/".equals(clazzName)) {
					// 跳转到首页
					final String homePath = settings.getHomePath(rRequest);
					if (homePath != null && homePath.startsWith(filterPath)) {
						if ((wrapper = _wrappers.get(homePath)) != null) {
							break l;
						}
						clazzName = homePath.substring(filterPath.length());
						// home类不放入缓存
						homeMark = true;
					}
				}
				if (StringUtils.hasText(clazzName)) {
					String pagePackage = null;
					int pos;
					if ((pos = clazzName.lastIndexOf("/") + 1) > 0) {
						final Map<String, String> packages = settings.getFilterPackages();
						if (packages != null) {
							pagePackage = packages.get(clazzName.substring(0, pos - 1));
						}
						clazzName = clazzName.substring(pos);
					}
					clazzName = StringUtils.replace(clazzName, "-", ".");
					if (pagePackage != null) {
						clazzName = StringUtils.hasText(clazzName) ? (pagePackage + "." + clazzName)
								: pagePackage;
					}
					try {
						wrapper = new PageWrapper(
								(Class<? extends AbstractMVCPage>) ClassUtils.forName(clazzName));
					} catch (final ClassNotFoundException e) {
					}
				}
				if (wrapper != null && !homeMark) {
					_wrappers.put(lookupPath, wrapper);
				}
			}

			if (wrapper != null) {
				pageDocument = getPageDocumentAndCreate(wrapper.pageClass, rRequest);
				AbstractMVCPage abstractMVCPage;
				if (pageDocument != null
						&& (abstractMVCPage = PageParameter.get(rRequest, pageDocument).getPage()) != null) {
					abstractMVCPage.setLookupPath(lookupPath);
				}
			}
		}
		return pageDocument;
	}

	private static Map<String, PageDocument> _documents = new ConcurrentHashMap<String, PageDocument>();

	public static PageDocument getPageDocument(final String docHash) {
		return docHash != null ? _documents.get(docHash) : null;
	}

	@SuppressWarnings("unchecked")
	public synchronized static PageDocument getPageDocumentAndCreate(Object sourceObject,
			final PageRequestResponse rRequest) {
		if (sourceObject == null) {
			return null;
		}

		String docHash = null;
		if (sourceObject instanceof File && ((File) sourceObject).exists()) {
			docHash = ObjectUtils.hashStr(sourceObject);
		} else if (sourceObject instanceof Object[]) {
			docHash = (String) ((Object[]) sourceObject)[1];
			sourceObject = ((Object[]) sourceObject)[0];
		} else if (sourceObject instanceof Class<?>) {
			docHash = ((Class<?>) sourceObject).getName();
		}

		if (docHash == null) {
			return null;
		}
		PageDocument document = _documents.get(docHash);
		if (document != null && document.isModified()) {
			document = null;
		}
		if (document == null) {
			try {
				if (sourceObject instanceof File) {
					_documents.put(docHash, document = new PageDocument((File) sourceObject, rRequest));
				} else if (sourceObject instanceof Class<?>) {
					document = AbstractMVCPage.createPageDocument(
							(Class<? extends AbstractMVCPage>) sourceObject, rRequest);
					if (document != null) {
						_documents.put(docHash, document);
					}
				} else if (sourceObject instanceof InputStream) {
					_documents.put(docHash, document = new PageDocument((InputStream) sourceObject,
							rRequest));
				}
			} catch (final Exception e) {
				throw MVCException.of(e);
			}
		} else {
			document.setFirstCreated(false);
		}
		return document;
	}

	public static PageDocument getPageDocumentByPath(final PageRequestResponse rRequest,
			final String xmlPath) {
		return getPageDocumentAndCreate(new File(MVCUtils.getRealPath(xmlPath)), rRequest);
	}

	private static class PageWrapper {
		Class<? extends AbstractMVCPage> pageClass;

		int priority = 0;

		PageWrapper(final Class<? extends AbstractMVCPage> pageClass, final int priority) {
			this.pageClass = pageClass;
			this.priority = priority;
		}

		PageWrapper(final Class<? extends AbstractMVCPage> pageClass) {
			this(pageClass, 0);
		}
	}
}
