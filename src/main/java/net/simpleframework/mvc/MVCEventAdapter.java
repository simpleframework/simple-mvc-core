package net.simpleframework.mvc;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class MVCEventAdapter implements ServletContextAttributeListener, ServletContextListener,
		HttpSessionListener, HttpSessionAttributeListener, ServletRequestListener {

	private static final List<EventListener> servletContextAttributeListeners = new ArrayList<EventListener>();

	private static final List<EventListener> servletContextListeners = new ArrayList<EventListener>();

	private static final List<EventListener> httpSessionAttributeListeners = new ArrayList<EventListener>();

	private static final List<EventListener> httpSessionListeners = new ArrayList<EventListener>();

	private static final List<EventListener> servletRequestListeners = new ArrayList<EventListener>();

	public MVCEventAdapter() {
	}

	public void addListener(final EventListener listener) {
		if (listener instanceof ServletContextAttributeListener) {
			addListener(servletContextAttributeListeners, listener);
		}
		if (listener instanceof ServletContextListener) {
			addListener(servletContextListeners, listener);
		}
		if (listener instanceof HttpSessionAttributeListener) {
			addListener(httpSessionAttributeListeners, listener);
		}
		if (listener instanceof HttpSessionListener) {
			addListener(httpSessionListeners, listener);
		}
		if (listener instanceof ServletRequestListener) {
			addListener(servletRequestListeners, listener);
		}
	}

	private void addListener(final List<EventListener> listeners, final EventListener listener) {
		synchronized (listeners) {
			if (!listeners.contains(listener)) {
				listeners.add(listener);
			}
		}
	}

	public void addListeners(final List<?> listeners) {
		for (final Iterator<?> iter = listeners.iterator(); iter.hasNext();) {
			addListener((EventListener) iter.next());
		}
	}

	// ServletContextListener

	@Override
	public void contextInitialized(final ServletContextEvent servletContextEvent) {
		synchronized (servletContextListeners) {
			for (int i = 0; i < servletContextListeners.size(); ++i) {
				final ServletContextListener listener = (ServletContextListener) servletContextListeners
						.get(i);
				listener.contextInitialized(servletContextEvent);
			}
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent servletContextEvent) {
		synchronized (servletContextListeners) {
			for (int i = servletContextListeners.size() - 1; i >= 0; --i) {
				final ServletContextListener listener = (ServletContextListener) servletContextListeners
						.get(i);
				listener.contextDestroyed(servletContextEvent);
			}
		}
	}

	// ServletContextAttributeListener

	@Override
	public void attributeAdded(final ServletContextAttributeEvent servletContextAttributeEvent) {
		synchronized (servletContextAttributeListeners) {
			for (int i = 0; i < servletContextAttributeListeners.size(); ++i) {
				final ServletContextAttributeListener listener = (ServletContextAttributeListener) servletContextAttributeListeners
						.get(i);
				listener.attributeAdded(servletContextAttributeEvent);
			}
		}
	}

	@Override
	public void attributeRemoved(final ServletContextAttributeEvent servletContextAttributeEvent) {
		synchronized (servletContextAttributeListeners) {
			for (int i = 0; i < servletContextAttributeListeners.size(); ++i) {
				final ServletContextAttributeListener listener = (ServletContextAttributeListener) servletContextAttributeListeners
						.get(i);
				listener.attributeRemoved(servletContextAttributeEvent);
			}
		}
	}

	@Override
	public void attributeReplaced(final ServletContextAttributeEvent servletContextAttributeEvent) {
		synchronized (servletContextAttributeListeners) {
			for (int i = 0; i < servletContextAttributeListeners.size(); ++i) {
				final ServletContextAttributeListener listener = (ServletContextAttributeListener) servletContextAttributeListeners
						.get(i);
				listener.attributeReplaced(servletContextAttributeEvent);
			}
		}
	}

	// HttpSessionListener

	@Override
	public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
		synchronized (httpSessionListeners) {
			for (int i = 0; i < httpSessionListeners.size(); ++i) {
				final HttpSessionListener listener = (HttpSessionListener) httpSessionListeners.get(i);
				listener.sessionCreated(httpSessionEvent);
			}
		}
	}

	@Override
	public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
		synchronized (httpSessionListeners) {
			for (int i = httpSessionListeners.size() - 1; i >= 0; --i) {
				final HttpSessionListener listener = (HttpSessionListener) httpSessionListeners.get(i);
				listener.sessionDestroyed(httpSessionEvent);
			}
		}
	}

	// HttpSessionAttributeListener

	@Override
	public void attributeAdded(final HttpSessionBindingEvent httpSessionBindingEvent) {
		synchronized (httpSessionAttributeListeners) {
			for (int i = 0; i < httpSessionAttributeListeners.size(); ++i) {
				final HttpSessionAttributeListener listener = (HttpSessionAttributeListener) httpSessionAttributeListeners
						.get(i);
				listener.attributeAdded(httpSessionBindingEvent);
			}
		}
	}

	@Override
	public void attributeRemoved(final HttpSessionBindingEvent httpSessionBindingEvent) {
		synchronized (httpSessionAttributeListeners) {
			for (int i = 0; i < httpSessionAttributeListeners.size(); ++i) {
				final HttpSessionAttributeListener listener = (HttpSessionAttributeListener) httpSessionAttributeListeners
						.get(i);
				listener.attributeRemoved(httpSessionBindingEvent);
			}
		}
	}

	@Override
	public void attributeReplaced(final HttpSessionBindingEvent httpSessionBindingEvent) {
		synchronized (httpSessionAttributeListeners) {
			for (int i = 0; i < httpSessionAttributeListeners.size(); ++i) {
				final HttpSessionAttributeListener listener = (HttpSessionAttributeListener) httpSessionAttributeListeners
						.get(i);
				listener.attributeReplaced(httpSessionBindingEvent);
			}
		}
	}

	// ServletRequestListener

	@Override
	public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
		synchronized (servletRequestListeners) {
			for (int i = 0; i < servletRequestListeners.size(); ++i) {
				final ServletRequestListener listener = (ServletRequestListener) servletRequestListeners
						.get(i);
				listener.requestDestroyed(servletRequestEvent);
			}
		}
	}

	@Override
	public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
		synchronized (servletRequestListeners) {
			for (int i = 0; i < servletRequestListeners.size(); ++i) {
				final ServletRequestListener listener = (ServletRequestListener) servletRequestListeners
						.get(i);
				listener.requestInitialized(servletRequestEvent);
			}
		}
	}
}
