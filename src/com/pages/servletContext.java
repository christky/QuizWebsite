package com.pages;

import com.dbinterface.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


/**
 * Application Lifecycle Listener implementation class servletContext
 *
 */
@WebListener
public class servletContext implements ServletContextListener {

	

    /**
     * Default constructor. 
     */
    public servletContext() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
    	String accounts = "Kelsey";
    	arg0.getServletContext().setAttribute("accounts", accounts);;
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }
	
}