package net.vicp.lylab.web.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.NonCloneableBaseObject;

/**
 * Application life cycle listener
 */
public class ServerLifeCycle extends NonCloneableBaseObject implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public ServerLifeCycle() {
		
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		CoreDef.rootPath = sce.getServletContext().getRealPath("/");
		CoreDef.config.reload(CoreDef.rootPath + "WEB-INF" + File.separator + "classes" + File.separator + "config.txt");
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		CoreDef.config.deepClose();
	}

}
