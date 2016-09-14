package net.simpleframework.mvc;

import static net.simpleframework.common.I18n.$m;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.simpleframework.common.BeanUtils;
import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.object.ObjectUtils;
import net.simpleframework.ctx.common.xml.XmlDocument;
import net.simpleframework.ctx.common.xml.XmlElement;
import net.simpleframework.ctx.script.IScriptEval;
import net.simpleframework.ctx.script.ScriptEvalUtils;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentException;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ComponentRegistryFactory;
import net.simpleframework.mvc.component.ComponentUtils;
import net.simpleframework.mvc.component.IComponentRegistry;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class PageDocument extends XmlDocument {
	private Class<? extends AbstractMVCPage> pageClass;

	private File documentFile;

	private PageBean pageBean;

	private long lastModified;

	private boolean firstCreated = true;

	private Map<String, AbstractComponentBean> componentsCache;

	public PageDocument(final File documentFile, final PageRequestResponse rRequest)
			throws IOException {
		super(new FileInputStream(documentFile));
		this.documentFile = documentFile;
		this.lastModified = documentFile.lastModified();
		init(rRequest);
	}

	public PageDocument(final InputStream inputStream, final PageRequestResponse rRequest)
			throws IOException {
		super(inputStream);
		init(rRequest);
	}

	public PageDocument(final Class<? extends AbstractMVCPage> mvcpageClass,
			final InputStream inputStream, final PageRequestResponse rRequest) throws Exception {
		super(inputStream);
		this.pageClass = mvcpageClass;
		init(rRequest);
	}

	public File getDocumentFile() {
		return documentFile;
	}

	public PageBean getPageBean() {
		return pageBean;
	}

	public Class<?> getPageClass() {
		return pageClass;
	}

	public boolean isFirstCreated() {
		return firstCreated;
	}

	public void setFirstCreated(final boolean firstCreated) {
		this.firstCreated = firstCreated;
	}

	@SuppressWarnings("serial")
	private void init(final PageRequestResponse rRequest) {
		final PageParameter pp = PageParameter.get(rRequest, this);

		pageBean = (PageBean) new PageBean(this).setElement(getRoot());
		pp.setRequestAttr(DECLARED_COMPONENTs,
				componentsCache = new LinkedHashMap<String, AbstractComponentBean>() {
					@Override
					public AbstractComponentBean put(final String key,
							final AbstractComponentBean value) {
						ComponentUtils.putComponent(value);
						return super.put(key, value);
					}
				});

		try {
			final AbstractMVCPage abstractMVCPage = singleton(pageClass);
			if (abstractMVCPage != null) {
				abstractMVCPage.pageDocument = this;
				abstractMVCPage.onCreate(pp);
				final Boolean b = (Boolean) rRequest.getRequestAttr(AbstractMVCPage.NULL_PAGEDOCUMENT);
				if (b != null && b.booleanValue()) {
					rRequest.removeRequestAttr(AbstractMVCPage.NULL_PAGEDOCUMENT);
					return;
				}
			}

			IScriptEval scriptEval = null;
			final XmlElement root = getRoot();
			XmlElement element = root.element(TAG_EVAL_SCOPE);
			if (element != null) {
				final String s = element.getText();
				final EEvalScope evalType = StringUtils.hasText(s) ? EEvalScope.valueOf(s)
						: EEvalScope.none;
				pageBean.setEvalScope(evalType);
				if (evalType != EEvalScope.none) {
					scriptEval = pp.createScriptEval();
				}
			}

			element = root.element(TAG_HANDLER_CLASS);
			if (element != null) {
				final String handlerClass = ScriptEvalUtils.replaceExpr(scriptEval, element.getText());
				if (StringUtils.hasText(handlerClass)) {
					pageBean.setHandlerClass(handlerClass);
				}
			}

			Iterator<?> it = root.elementIterator();
			while (it.hasNext()) {
				element = (XmlElement) it.next();
				final String name = element.getTagName();
				if (name.equals(TAG_EVAL_SCOPE) || name.equals(TAG_HANDLER_CLASS)
						|| name.equals(TAG_COMPONENTS)) {
					continue;
				}
				if (name.equals(TAG_IMPORT_PAGE) || name.equals(TAG_IMPORT_JAVASCRIPT)
						|| name.equals(TAG_IMPORT_CSS)) {
					final Set<String> l = new LinkedHashSet<String>();
					final Iterator<?> values = element.elementIterator(TAG_VALUE);
					while (values.hasNext()) {
						final String value = ScriptEvalUtils.replaceExpr(scriptEval,
								((XmlElement) values.next()).getText());
						l.add(MVCUtils.doPageUrl(pp, value));
					}
					final int size = l.size();
					if (size == 0) {
						continue;
					}
					final String[] strings = l.toArray(new String[size]);
					if (name.equals(TAG_IMPORT_PAGE)) {
						pageBean.setImportPage(strings);
					} else if (name.equals(TAG_IMPORT_JAVASCRIPT)) {
						pageBean.setImportJavascript(strings);
					} else {
						pageBean.setImportCSS(strings);
					}
				} else {
					final String value = element.getText();
					if (name.equals(TAG_SCRIPT_INIT)) {
						if (scriptEval != null && StringUtils.hasText(value)) {
							pageBean.setScriptInit(value);
							scriptEval.eval(value);
						}
					} else {
						BeanUtils.setProperty(pageBean, name,
								ScriptEvalUtils.replaceExpr(scriptEval, value));
					}
				}
			}

			final XmlElement components = root.element(TAG_COMPONENTS);
			if (components == null) {
				return;
			}

			final ComponentRegistryFactory factory = ComponentRegistryFactory.get();
			it = components.elementIterator();
			while (it.hasNext()) {
				final XmlElement element2 = (XmlElement) it.next();
				final String tagName = element2.getTagName();
				final IComponentRegistry registry = factory.getComponentRegistry(tagName);
				if (registry == null) {
					throw ComponentException.of($m("PageDocument.0", tagName));
				}
				if (!registry.getPageResourceProvider().equals(getPageResourceProvider())) {
					throw ComponentException.of($m("PageDocument.2"));
				}

				final AbstractComponentBean componentBean = registry.createComponentBean(pp, element2);
				if (componentBean != null) {
					final String componentName = ComponentParameter.get(rRequest, componentBean)
							.getComponentName();
					if (!StringUtils.hasText(componentName)) {
						throw ComponentException.of($m("PageDocument.1"));
					}
					componentsCache.put(componentName, componentBean);
				}
			}
		} finally {
			pp.removeRequestAttr(DECLARED_COMPONENTs);
		}
	}

	private Collection<PageDocument> getImportDocuments(final PageParameter pp) {
		final String rKey = "documents_" + hashId();
		@SuppressWarnings("unchecked")
		ArrayList<PageDocument> documents = (ArrayList<PageDocument>) pp.getRequestAttr(rKey);
		if (documents == null) {
			documents = new ArrayList<PageDocument>();
			final String[] importPages = (String[]) pp.getBeanProperty("importPage");
			if (importPages != null) {
				for (final String importPage : importPages) {
					Object pObject = new File(MVCUtils.getRealPath(importPage));
					if (!((File) pObject).exists()) {
						final InputStream inputStream = ClassUtils.getResourceAsStream(importPage);
						if (inputStream != null) {
							pObject = new Object[] { inputStream, importPage };
						} else {
							try {
								pObject = ClassUtils.forName(importPage);
								continue;
							} catch (final ClassNotFoundException e) {
								getLog().warn(e);
							}
						}
					}
					final PageDocument document = PageDocumentFactory.getPageDocumentAndCreate(pObject,
							pp);
					if (document != null) {
						documents.add(document);
					}
				}
			}
			if (pageClass != null) {
				final Class<?> superClass = pageClass.getSuperclass();
				if (!AbstractMVCPage.class.equals(superClass)) {
					final PageDocument document = PageDocumentFactory
							.getPageDocumentAndCreate(superClass, pp);
					if (document != null) {
						documents.add(document);
					}
				}
			}
			pp.setRequestAttr(rKey, documents);
		}
		return documents;
	}

	private static final String DECLARED_COMPONENTs = "$declared_components";

	@SuppressWarnings("unchecked")
	public Map<String, AbstractComponentBean> getComponentBeans(final PageParameter pp) {
		final Map<String, AbstractComponentBean> componentBeans = (Map<String, AbstractComponentBean>) pp
				.getRequestAttr(DECLARED_COMPONENTs);
		if (componentBeans != null) {
			return componentBeans;
		}
		return getRunningComponentBeans(pp, true);
	}

	@SuppressWarnings({ "unchecked", "serial" })
	private Map<String, AbstractComponentBean> getRunningComponentBeans(PageParameter pp,
			final boolean cache) {
		PageDocument pageDocument = null;
		if (pp instanceof ComponentParameter) {
			pageDocument = ((ComponentParameter) pp).getDocumentRef();
		}
		if (pageDocument == null) {
			pageDocument = pp.getPageDocument();
		}
		// pParameter可能为ComponentParameter对象
		pp = PageParameter.get(pp, pageDocument);

		final String docKey = "$rc_" + pageDocument.hashId();
		Map<String, AbstractComponentBean> componentBeans;
		if (cache) {
			componentBeans = (Map<String, AbstractComponentBean>) pp.getRequestAttr(docKey);
			if (componentBeans != null) {
				return componentBeans;
			}
		}

		componentBeans = new LinkedHashMap<String, AbstractComponentBean>() {
			@Override
			public AbstractComponentBean put(final String key,
					final AbstractComponentBean componentBean) {
				// 不在PageDocument中的components，则缓存到session中
				final String hashId = componentBean.hashId();
				if (ComponentUtils.getComponent(hashId) == null) {
					SessionCache.lput(hashId, componentBean);
				}
				return super.put(key, componentBean);
			}
		};
		if (cache) {
			pp.setRequestAttr(docKey, componentBeans);
		}

		for (final PageDocument document : getImportDocuments(pp)) {
			final Map<String, AbstractComponentBean> componentBeans2 = document
					.getRunningComponentBeans(PageParameter.get(pp, document), false);
			componentBeans.putAll(componentBeans2);
		}

		final Map<String, AbstractComponentBean> oComponentBeans = pageDocument.componentsCache;
		if (pageDocument.isFirstCreated()
				|| pageDocument.getPageBean().getEvalScope() != EEvalScope.request) {
			componentBeans.putAll(oComponentBeans);
		} else {
			final IScriptEval scriptEval = pp.createScriptEval();
			final String scriptInit = pageDocument.getScriptInit(pp);
			if (StringUtils.hasText(scriptInit)) {
				scriptEval.eval(scriptInit);
			}
			for (final Map.Entry<String, AbstractComponentBean> entry : oComponentBeans.entrySet()) {
				final AbstractComponentBean componentBean = entry.getValue();
				final XmlElement element = componentBean.getElement();
				if (element == null) {
					componentBeans.put(entry.getKey(), componentBean);
				} else {
					final AbstractComponentBean componentBean2 = componentBean.getComponentRegistry()
							.createComponentBean(pp, element);
					componentBeans.put(entry.getKey(), componentBean2);
				}
			}
		}
		return componentBeans;
	}

	boolean isModified() {
		final File documentFile = getDocumentFile();
		return documentFile != null && documentFile.lastModified() != lastModified;
	}

	public Collection<String> getImportJavascript(final PageParameter pp) {
		final LinkedHashSet<String> jsColl = new LinkedHashSet<String>();
		for (final PageDocument document : getImportDocuments(pp)) {
			final Collection<String> coll = document
					.getImportJavascript(PageParameter.get(pp, document));
			if (coll != null) {
				jsColl.addAll(coll);
			}
		}
		final String[] importJavascript = (String[]) pp.getBeanProperty("importJavascript");
		if (importJavascript != null) {
			for (final String js : importJavascript) {
				jsColl.add(js);
			}
		}
		return jsColl;
	}

	public Collection<String> getImportCSS(final PageParameter pp) {
		final LinkedHashSet<String> cssColl = new LinkedHashSet<String>();
		for (final PageDocument document : getImportDocuments(pp)) {
			final Collection<String> coll = document.getImportCSS(PageParameter.get(pp, document));
			if (coll != null) {
				cssColl.addAll(coll);
			}
		}
		final String[] importCSS = (String[]) pp.getBeanProperty("importCSS");
		if (importCSS != null) {
			for (final String css : importCSS) {
				cssColl.add(css);
			}
		}
		return cssColl;
	}

	public String getTitle(final PageParameter pp) {
		final String title = (String) pp.getBeanProperty("title");
		if (!StringUtils.hasText(title)) {
			for (final PageDocument document : getImportDocuments(pp)) {
				final String title2 = document.getTitle(PageParameter.get(pp, document));
				if (StringUtils.hasText(title2)) {
					return title2;
				}
			}
		}
		return title;
	}

	public String getJsLoadedCallback(final PageParameter pp) {
		String jsLoadedCallback = StringUtils.blank(pp.getBeanProperty("jsLoadedCallback"));
		for (final PageDocument document : getImportDocuments(pp)) {
			final String js = document.getJsLoadedCallback(PageParameter.get(pp, document));
			if (StringUtils.hasText(js)) {
				jsLoadedCallback += js;
			}
		}
		return jsLoadedCallback;
	}

	public String getScriptInit(final PageParameter pp) {
		String scriptInit = StringUtils.blank(pp.getBeanProperty("scriptInit"));
		for (final PageDocument document : getImportDocuments(pp)) {
			final String script = document.getScriptInit(PageParameter.get(pp, document));
			if (StringUtils.hasText(script)) {
				scriptInit += script;
			}
		}
		return scriptInit;
	}

	public IPageResourceProvider getPageResourceProvider() {
		return PageResourceProviderRegistry.get()
				.getPageResourceProvider(pageBean.getResourceProvider());
	}

	private IPageHandler pageHandle;

	public IPageHandler getPageHandler(final PageParameter pp) {
		if (pageHandle == null) {
			String hdlstr = pageBean.getHandlerClass();
			AbstractMVCPage pageView = null;
			if (!StringUtils.hasText(hdlstr) && (pageView = singleton(pageClass)) != null) {
				Class<?> pageClass = pageView.getClass().getSuperclass();
				while (!pageClass.equals(AbstractMVCPage.class)) {
					final PageBean pageBean2 = PageDocumentFactory
							.getPageDocumentAndCreate(pageClass, pp).getPageBean();
					final String hdlstr2 = pageBean2.getHandlerClass();
					if (StringUtils.hasText(hdlstr2)) {
						pageBean.setHandlerClass(hdlstr = hdlstr2);
						pageBean.setHandlerMethod(pageBean2.getHandlerMethod());
						break;
					}
					pageClass = pageClass.getSuperclass();
				}
			}
			if (StringUtils.hasText(hdlstr)) {
				try {
					final Class<?> handlerClass = ClassUtils.forName(hdlstr);
					pageHandle = (IPageHandler) (AbstractMVCPage.class.isAssignableFrom(handlerClass)
							? new AbstractMVCPage.PageLoad() : handlerClass.newInstance());
				} catch (final Exception e) {
					throw MVCException.of(e);
				}
			} else {
				pageHandle = pageView != null ? new AbstractMVCPage.PageLoad()
						: new DefaultPageHandler();
			}
		}
		return pageHandle;
	}

	private String _hashId;

	public String hashId() {
		return _hashId == null ? (_hashId = pageClass != null ? pageClass.getName()
				: ObjectUtils.hashStr(getDocumentFile())) : _hashId;
	}

	@Override
	public boolean equals(final Object obj) {
		final File documentFile = getDocumentFile();
		if (documentFile != null && obj instanceof PageDocument) {
			return documentFile.equals(((PageDocument) obj).getDocumentFile());
		} else {
			return super.equals(obj);
		}
	}

	final static String TAG_HANDLER_CLASS = "handlerClass";
	final static String TAG_COMPONENTS = "components";
	final static String TAG_SCRIPT_INIT = "scriptInit";
	final static String TAG_EVAL_SCOPE = "evalScope";
	final static String TAG_IMPORT_PAGE = "importPage";
	final static String TAG_IMPORT_JAVASCRIPT = "importJavascript";
	final static String TAG_IMPORT_CSS = "importCSS";
	final static String TAG_VALUE = "value";
}
