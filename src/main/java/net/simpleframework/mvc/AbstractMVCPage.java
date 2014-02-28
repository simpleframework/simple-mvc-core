package net.simpleframework.mvc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.simpleframework.common.ClassUtils;
import net.simpleframework.common.Convert;
import net.simpleframework.common.DateUtils;
import net.simpleframework.common.I18n;
import net.simpleframework.common.IoUtils;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.ArrayUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.common.coll.ParameterMap;
import net.simpleframework.common.logger.Log;
import net.simpleframework.common.logger.LogFactory;
import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.lib.org.jsoup.nodes.Element;
import net.simpleframework.mvc.common.element.Meta;
import net.simpleframework.mvc.component.AbstractComponentBean;
import net.simpleframework.mvc.component.ComponentHandlerException;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.IComponentHandler;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractMVCPage extends AbstractMVCHandler {

	protected PageDocument pageDocument;

	private String lookupPath;

	public AbstractMVCPage() {
		this.enableAttributes();
	}

	/**
	 * 页面创建时运行
	 * 
	 * @param pParameter
	 */
	protected void onCreate(final PageParameter pp) {
	}

	/**
	 * page类的初始化方法，每次调用该页面时运行
	 * 
	 * @param pageParameter
	 */
	protected void onForward(final PageParameter pp) {
	}

	protected static final String[] NULL_ARR = new String[0];

	public String[] getDefaultCssPath(final PageParameter pp) {
		return null;
	}

	public String[] getDefaultJavascriptPath(final PageParameter pp) {
		return null;
	}

	public void addImportPage(final Class<? extends AbstractMVCPage>... pageClass) {
		if (pageClass == null || pageClass.length == 0) {
			return;
		}
		final LinkedHashSet<String> l = new LinkedHashSet<String>();
		final PageBean pageBean = getPageBean();
		final String[] oImportPage = pageBean.getImportPage();
		if (oImportPage != null) {
			l.addAll(ArrayUtils.asList(oImportPage));
		}
		for (int i = 0; i < pageClass.length; i++) {
			l.add(pageClass[i].getName().replace(".", "/") + ".xml");
		}
		pageBean.setImportPage(l.toArray(new String[l.size()]));
	}

	public PageBean getPageBean() {
		return pageDocument.getPageBean();
	}

	@SuppressWarnings("unchecked")
	private Map<Class<?>, ParameterMap> getHtmlViewVariables(final PageParameter pp) {
		Map<Class<?>, ParameterMap> htmlViewVariables = (Map<Class<?>, ParameterMap>) pp
				.getRequestAttr("_HTMLVIEW_VARIABLES");
		if (htmlViewVariables == null) {
			pp.setRequestAttr("_HTMLVIEW_VARIABLES",
					htmlViewVariables = new HashMap<Class<?>, ParameterMap>());
		}
		return htmlViewVariables;
	}

	/**
	 * 添加模板中定义的html文件变量
	 * 
	 * @param pageClass
	 * @param variable
	 * @param htmlFilename
	 */
	protected void addHtmlViewVariable(final PageParameter pp, final Class<?> pageClass,
			final String variable, final String htmlFilename) {
		final Map<Class<?>, ParameterMap> htmlViewVariables = getHtmlViewVariables(pp);
		ParameterMap htmlViews = htmlViewVariables.get(pageClass);
		if (htmlViews == null) {
			htmlViewVariables.put(pageClass, htmlViews = new ParameterMap());
		}
		for (final ParameterMap m : htmlViewVariables.values()) {
			if (m.remove(variable) != null) {
				break;
			}
		}
		htmlViews.put(variable, htmlFilename);
	}

	protected void addHtmlViewVariable(final PageParameter pp, final Class<?> pageClass,
			final String variable) {
		addHtmlViewVariable(pp, pageClass, variable, getClassName(pageClass) + ".html");
	}

	protected String getMethod(final PageParameter pp) {
		return pp.getParameter("method");
	}

	/**
	 * 页面的核心处理方法，该方法将委托给onForward和toHTML分别处理页面逻辑及展示
	 * 
	 * 或则基于模板技术替代toHTML
	 * 
	 * @param pp
	 * @return
	 */
	public IForward forward(final PageParameter pp) {
		onForward(pp);
		final String methodStr = getMethod(pp);
		if (StringUtils.hasText(methodStr)) {
			try {
				return (IForward) ClassUtils.invoke(getClass()
						.getMethod(methodStr, PageParameter.class), this, pp);
			} catch (final NoSuchMethodException e) {
				log.warn(e);
			}
		}
		try {
			return new TextForward(getPageForward(pp, getClass(), getVariables(pp)));
		} catch (final IOException e) {
			log.warn(e);
		}
		return null;
	}

	/**
	 * 子类继承，输出page的html
	 * 
	 * @param pParameter
	 * @param pageClass
	 * @param variables
	 * @param currentVariable
	 * @return
	 * @throws IOException
	 */
	protected String toHtml(final PageParameter pp,
			final Class<? extends AbstractMVCPage> pageClass, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		if (getClass().equals(pageClass)) {
			return toHtml(pp, variables, currentVariable);
		}
		return null;
	}

	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		return null;
	}

	/**
	 * 模板的核心方法，用户覆盖此函数可以设置不同的模板引擎
	 * 
	 * @param htmlStream
	 * @param variables
	 * @return
	 * @throws IOException
	 */
	protected String replaceExpr(final PageParameter pp, final InputStream htmlStream,
			final Map<String, Object> variables) throws IOException {
		return IoUtils.getStringFromInputStream(htmlStream, getChartset());
	}

	protected static final String $html = "$html$";

	private String getPageForward(final PageParameter pp,
			final Class<? extends AbstractMVCPage> pageClass, final Map<String, Object> variables)
			throws IOException {
		@SuppressWarnings("unchecked")
		final Class<? extends AbstractMVCPage> superPageClass = (Class<? extends AbstractMVCPage>) pageClass
				.getSuperclass();
		if (!isExtend(pp, pageClass) || superPageClass.equals(AbstractMVCPage.class)) {
			final InputStream htmlStream = getResource(pageClass, ".html");
			if (htmlStream != null) {
				return replaceExpr(pp, htmlStream, variables);
			} else {
				final String html = toHtml(pp, pageClass, variables, null);
				return StringUtils.hasText(html) ? html : (String) variables.get($html);
			}
		} else {
			final ParameterMap htmlViews = getHtmlViewVariables(pp).get(pageClass);
			if (htmlViews == null || htmlViews.size() == 0) {
				final InputStream htmlStream = getResource(pageClass, ".html");
				final String html = htmlStream != null ? replaceExpr(pp, htmlStream, variables)
						: toHtml(pp, pageClass, variables, null);
				if (html != null) {
					variables.put($html, html);
				}
			} else {
				for (final Map.Entry<String, String> entry : htmlViews.entrySet()) {
					final String key = entry.getKey();
					final String filename = entry.getValue();
					final InputStream htmlStream = getResource(pageClass, filename);
					if (htmlStream != null) {
						variables.put(key, replaceExpr(pp, htmlStream, variables));
					} else {
						final String text = toHtml(pp, pageClass, variables, key);
						if (StringUtils.hasText(text)) {
							variables.put(key, text);
						}
					}
					if (!variables.containsKey(key)) {
						variables.put(key, "");
					}
				}
			}
			return getPageForward(pp, superPageClass, variables);
		}
	}

	public String getResourceHomePath() {
		return getResourceHomePath(getClass());
	}

	public String getResourceHomePath(final Class<?> pageClass) {
		return pageDocument.getPageResourceProvider().getResourceHomePath(pageClass);
	}

	public String getCssResourceHomePath(final PageParameter pp) {
		return getCssResourceHomePath(pp, getClass());
	}

	public String getCssResourceHomePath(final PageParameter pp, final Class<?> pageClass) {
		return pp.getCssResourceHomePath(pageClass);
	}

	public String getInitJavascriptCode(final PageParameter pp) {
		return null;
	}

	/**
	 * 获取页面依赖的组件，一般不需要覆盖，在addComponentBean时自动依赖
	 * 
	 * @param pp
	 * @return
	 */
	public String[] getDependentComponents(final PageParameter pp) {
		return null;
	}

	private static final String KEY_VARIABLES = "_variables";

	/**
	 * 定义模板中可见的变量
	 * 
	 * @param pageParameter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getVariables(final PageParameter pp) {
		Map<String, Object> variables = (Map<String, Object>) pp.getRequestAttr(KEY_VARIABLES);
		if (variables == null) {
			pp.setRequestAttr(KEY_VARIABLES, variables = createVariables(pp));
		}
		return variables;
	}

	protected Map<String, Object> createVariables(final PageParameter pp) {
		return new KVMap().setNullVal("").add("page", this).add("parameter", pp)
				.add("pagePath", getPagePath()).add("StringUtils", StringUtils.class)
				.add("Convert", Convert.class).add("DateUtils", DateUtils.class)
				.add("I18n", I18n.class);
	}

	private static final Map<Class<? extends AbstractMVCPage>, String> urlCache;
	static {
		urlCache = new ConcurrentHashMap<Class<? extends AbstractMVCPage>, String>();
	}

	public void registUrl(final String url) {
		registUrl(url, getClass(), 0);
	}

	/**
	 * 注册page类和url的映射关系
	 * 
	 * @param url
	 * @param pageClass
	 */
	public static void registUrl(String url, final Class<? extends AbstractMVCPage> pageClass,
			final int priority) {
		final String homepath = settings.getFilterPath();
		if (homepath != null && homepath.length() > 1 && !url.startsWith(homepath)) {
			url = homepath + url;
		}
		PageDocumentFactory.addPageClass(url, pageClass, priority);
		urlCache.put(pageClass, url);
	}

	/**
	 * 获取页面的实例
	 * 
	 * @param pageClass
	 * @return
	 */
	public static <T extends AbstractMVCPage> T get(final Class<T> pageClass) {
		return singleton(pageClass);
	}

	/**
	 * 获取当前页面的实例
	 * 
	 * @param pageClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AbstractMVCPage> T get(final PageParameter pp) {
		return (T) pp.getPage();
	}

	/**
	 * 生成page类的访问url
	 * 
	 * @param clazz
	 * @param queryString
	 * @return
	 */
	public static String url(final Class<? extends AbstractMVCPage> clazz, final String queryString) {
		String url = urlCache.get(clazz);
		if (!StringUtils.hasText(url)) {
			url = settings.getFilterPath();
			if (!url.endsWith("/")) {
				url += "/";
			}
			final Map<String, String> pagePackages = settings.getFilterPackages();
			String className = clazz.getName();
			String val = null;
			if (pagePackages != null) {
				for (final Map.Entry<String, String> entry : pagePackages.entrySet()) {
					val = entry.getValue();
					if (className.startsWith(val)) {
						final String key = entry.getKey();
						url += key.startsWith("/") ? key.substring(1) : key;
						if (!url.endsWith("/")) {
							url += "/";
						}
						break;
					} else {
						val = null;
					}
				}
			}
			if (val != null) {
				className = className.equals(val) ? "" : className.substring(val.length() + 1);
			}
			urlCache.put(clazz, url += StringUtils.replace(className, ".", "-"));
		}
		if (StringUtils.hasText(queryString)) {
			url += "?" + queryString;
		}
		return url;
	}

	public static String url(final Class<? extends AbstractMVCPage> clazz) {
		return url(clazz, null);
	}

	/**
	 * 定义是否继承父类的html输出
	 * 
	 * @param pp
	 * @param pageClass
	 * @return
	 */
	protected boolean isExtend(final PageParameter pp,
			final Class<? extends AbstractMVCPage> pageClass) {
		return true;
	}

	protected String getPagePath() {
		return settings.getFilterPath();
	}

	protected String getChartset() {
		return settings.getCharset();
	}

	public String getLookupPath() {
		return lookupPath;
	}

	public void setLookupPath(final String lookupPath) {
		this.lookupPath = lookupPath;
	}

	public String getRole(final PageParameter pp) {
		return null;
	}

	/**
	 * 页面的标题，显示在浏览器的标题栏上
	 * 
	 * @param pp
	 * @return
	 */
	public String getTitle(final PageParameter pp) {
		String title = null;
		PageMapping pm;
		if ((pm = getClass().getAnnotation(PageMapping.class)) != null) {
			title = pm.title();
		}
		return title;
	}

	public String getFavicon(final PageParameter pp) {
		return null;
	}

	protected String getRedirectUrl(final PageParameter pp) {
		return null;
	}

	/**
	 * 设置页面的meta
	 * 
	 * @param pParameter
	 * @return
	 */
	public Collection<Meta> html_meta(final PageParameter pp) {
		final ArrayList<Meta> al = new ArrayList<Meta>();
		al.add(Meta.RENDERER_WEBKIT);
		al.add(Meta.GOOGLE_NOTRANSLATE);
		al.add(Meta.CLEARTYPE);
		return al;
	}

	public String html_css(final PageParameter pp) {
		return null;
	}

	public void html_normalise(final PageParameter pp, final Element element) {
	}

	/*--------------------------------- static ---------------------------------*/
	protected static String getClassName(final Class<?> pageClass) {
		return ObjectFactory.original(pageClass).getSimpleName();
	}

	protected static InputStream getResource(final Class<?> resourceClass, final String filename) {
		return ClassUtils.getResourceAsStream(resourceClass,
				filename.startsWith(".") ? getClassName(resourceClass) + filename : filename);
	}

	public static final String NULL_PAGEDOCUMENT = "@null_pagedocument";

	static PageDocument createPageDocument(final Class<? extends AbstractMVCPage> pageClass,
			final PageRequestResponse rRequest) {
		PageDocument pageDocument = null;
		InputStream inputStream = getResource(pageClass, ".xml");
		if (inputStream == null) {
			rRequest.setRequestAttr(NULL_PAGEDOCUMENT, Boolean.TRUE);
			inputStream = getResource(AbstractMVCPage.class, "page-null.xml");
		}
		try {
			pageDocument = new PageDocument(pageClass, inputStream, rRequest);
		} catch (final Exception e) {
			slog.error(e);
		}
		return pageDocument;
	}

	private static Log slog = LogFactory.getLogger(AbstractMVCPage.class);

	/*--------------------------------- components wrapper  ---------------------------------*/

	protected <T extends AbstractComponentBean> T addComponentBean(final PageParameter pp,
			final Map<String, Object> attris, final Class<T> beanClass) {
		return pp.addComponentBean(attris, beanClass);
	}

	protected <T extends AbstractComponentBean> T addComponentBean(final PageParameter pp,
			final String name, final Class<T> beanClass) {
		return pp.addComponentBean(name, beanClass);
	}

	@SuppressWarnings("unchecked")
	protected <T extends AbstractComponentBean> T addComponentBean(final PageParameter pp,
			final Class<T> beanClass, final Class<? extends IComponentHandler> handlerClass) {
		return (T) addComponentBean(pp, handlerClass.getSimpleName(), beanClass).setHandlerClass(
				handlerClass);
	}

	/**
	 * 
	 * @param pageParameter
	 * @param beanProperty
	 * @return
	 */
	public Object getPageBeanProperty(final PageParameter pp, final String beanProperty) {
		if ("role".equals(beanProperty)) {
			return getRole(pp);
		} else if ("title".equals(beanProperty)) {
			return getTitle(pp);
		} else if ("favicon".equals(beanProperty)) {
			return getFavicon(pp);
		}
		return null;
	}

	public static class PageLoad extends DefaultPageHandler {

		@Override
		public Object getBeanProperty(final PageParameter pp, final String beanProperty) {
			final Object property = AbstractMVCPage.get(pp).getPageBeanProperty(pp, beanProperty);
			return property != null ? property : super.getBeanProperty(pp, beanProperty);
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onPageLoad(final PageParameter pp, final Map<String, Object> dataBinding,
				final PageSelector selector) {
			final String handlerClass = (String) pp.getBeanProperty("handlerClass");
			if (!StringUtils.hasText(handlerClass)) {
				return;
			}
			try {
				((Class<AbstractMVCPage>) ClassUtils.forName(handlerClass)).getMethod(
						(String) pp.getBeanProperty("handlerMethod"), PageParameter.class, Map.class,
						PageSelector.class).invoke(AbstractMVCPage.get(pp), pp, dataBinding, selector);
			} catch (final Exception e) {
				throw ComponentHandlerException.of(e);
			}
		}
	}

	public IComponentHandler createComponentHandler(final ComponentParameter cp) {
		return null;
	}
}
