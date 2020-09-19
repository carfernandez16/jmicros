package com.jmicros.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class EmbeddedRESTServer {

    private static Logger logger = LogManager.getLogger(EmbeddedRESTServer.class);

    static final String APPLICATION_PATH = "/api";
    static final String CONTEXT_ROOT = "/";

    public EmbeddedRESTServer() {}

    public static void main( String[] args ) throws Exception
    {
        try
        {
            new EmbeddedRESTServer().run();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    public void run() throws Exception
    {
        final int port = 8080;
        final Server server = new Server(port);

        // Setup the basic Application "context" at "/".
        // This is also known as the handler tree (in Jetty speak).
        final ServletContextHandler context = new ServletContextHandler(
                server, CONTEXT_ROOT);

        // Setup RESTEasy's HttpServletDispatcher at "/api/*".
        final ServletHolder restEasyServlet = new ServletHolder(
                new HttpServletDispatcher());
        restEasyServlet.setInitParameter("resteasy.servlet.mapping.prefix",
                APPLICATION_PATH);
        restEasyServlet.setInitParameter("javax.ws.rs.Application",
                "com.jmicros.rest.FatJarApplication");
        restEasyServlet.setInitParameter("resteasy.providers", "org.jboss.resteasy.plugins.providers.StringTextStar," +
                "org.jboss.resteasy.plugins.providers.InputStreamProvider," +
                "org.jboss.resteasy.plugins.providers.ByteArrayProvider," +
                "org.jboss.resteasy.plugins.providers.DefaultTextPlain," +
                "org.jboss.resteasy.plugins.providers.DocumentProvider," +
                "org.jboss.resteasy.plugins.providers.DataSourceProvider");
        //context.addServlet(restEasyServlet, APPLICATION_PATH + "/*");
        context.addServlet(restEasyServlet, "/*");

        // Setup the DefaultServlet at "/".
        final ServletHolder defaultServlet = new ServletHolder(
                new DefaultServlet());
        context.addServlet(defaultServlet, CONTEXT_ROOT);

        server.start();
        server.join();
    }


}
