/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine.plugin;

import java.net.InetSocketAddress;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.rpgwizard.html5.engine.plugin.rest.EngineRestService;

/**
 *
 * @author Joshua Michael Daly
 */
public class EngineRunnable implements Runnable {

    private final String resourceBase;

    private Server server;

    public EngineRunnable(String resourceBase) {
        this.resourceBase = resourceBase;
    }

    @Override
    public void run() {
        server = new Server(new InetSocketAddress(System.getProperty("host", "0.0.0.0"), 8080));
        server.setStopAtShutdown(true);

        EngineRestService restService = new EngineRestService(resourceBase);
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(restService);

        ServletContainer servletContainer = new ServletContainer(resourceConfig);
        ServletHolder servletHolder = new ServletHolder(servletContainer);
        ServletContextHandler restHandler = new ServletContextHandler();
        restHandler.addServlet(servletHolder, "/*");

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[] { "index.html" });
        resourceHandler.setCacheControl("no-store,no-cache,must-revalidate");
        resourceHandler.setResourceBase(resourceBase);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, restHandler });
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void stop() throws Exception {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    public static void main(String[] args) {
        EngineRunnable runnable = new EngineRunnable(System.getProperty("org.rpgwizard.execution.path") + "/project");
        runnable.run();
    }

}
