/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine.plugin;

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

	@Override
	public void run() {
		server = new Server(8080);
		server.setStopAtShutdown(true);

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[]{"index.html"});
		resourceHandler.setCacheControl("no-store,no-cache,must-revalidate");
		resourceHandler.setResourceBase(resourceBase);

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{resourceHandler});
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
