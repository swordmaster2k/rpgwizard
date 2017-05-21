/**
 * Copyright (c) 2017, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.html5.engine.plugin;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

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

    public void run() {
        server = new Server(8080);
        server.setStopAtShutdown(true);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{"index.html"});

        resource_handler.setResourceBase(resourceBase);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{
            resource_handler,
            new DefaultHandler()
        });
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void stop() throws Exception {
        server.stop();
    }

}
