/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cef.OS;
import org.rpgwizard.html5.engine.plugin.EngineRunnable;
import org.rpgwizard.html5.engine.plugin.browser.EmbeddedBrowser;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class Standalone {

	public static boolean STANDALONE_MODE = false;

	private static Thread ENGINE_THREAD;
	private static EngineRunnable ENGINE_RUNNABLE;
	private static EmbeddedBrowser EMBEDDED_BROWSER;

	public static void main(String[] args) {
        Standalone.STANDALONE_MODE = true;
        
        String resourceBase = System.getProperty("org.rpgwizard.execution.path") + File.separator + "data";

        // Start the Embedded Jetty.
        ENGINE_RUNNABLE = new EngineRunnable(resourceBase);
        ENGINE_THREAD = new Thread(ENGINE_RUNNABLE);
        ENGINE_THREAD.start();

        javax.swing.SwingUtilities.invokeLater(() -> {
            // Show the JCEF browser window.
            EMBEDDED_BROWSER = new EmbeddedBrowser("Test", "http://localhost:8080", OS.isLinux(), false, 640, 480);
            EMBEDDED_BROWSER.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        ENGINE_RUNNABLE.stop();
                        EMBEDDED_BROWSER.stop();
                    } catch (Exception ex) {
                        Logger.getLogger(Standalone.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    // JCEF keeps hanging.
                    throw new RuntimeException("Forcefully shutting down JCEF - Can ignore this exception");
                }
            });
        });

    }
}
