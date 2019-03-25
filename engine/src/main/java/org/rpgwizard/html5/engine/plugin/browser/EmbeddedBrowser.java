/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.html5.engine.plugin.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.swing.JFrame;

import org.cef.CefApp;
import org.cef.CefApp.CefAppState;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefAppHandlerAdapter;
import org.cef.handler.CefKeyboardHandler;
import org.cef.handler.CefKeyboardHandlerAdapter;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

public final class EmbeddedBrowser extends JFrame {

    private static final long serialVersionUID = -5570653778104813836L;
    private final CefApp cefApp;
    private final CefClient cefClient;
    private final CefBrowser cefBrowser;
    private final Component browserUI;
    private Component devToolsUI;

    public EmbeddedBrowser(String title, String startURL, boolean useOSR, boolean isTransparent, int width, int height,
            boolean isFullScreen, File iconFile) {
        CefApp.addAppHandler(new CefAppHandlerAdapter(null) {
            @Override
            public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                // Shutdown the app if the native CEF part is terminated
                if (state == CefAppState.TERMINATED) {
                    // calling System.exit(0) appears to be causing assert
                    // errors,
                    // as its firing before all of the CEF objects shutdown.
                    // System.exit(0);

                    // TODO: hack to get around memory issue with JCEF.
                    Runtime.getRuntime().halt(0);
                }
            }
        });

        // CEF config.
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = useOSR;
        cefApp = CefApp.getInstance(settings);
        cefClient = cefApp.createClient();
        cefBrowser = cefClient.createBrowser(startURL, useOSR, isTransparent);
        browserUI = cefBrowser.getUIComponent();

        cefClient.addLoadHandler(new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser cb, boolean bln, boolean bln1, boolean bln2) {
            }

            @Override
            public void onLoadStart(CefBrowser cb, CefFrame cf, CefRequest.TransitionType tt) {
            }

            @Override
            public void onLoadEnd(CefBrowser arg0, CefFrame cf, int arg2) {
                cefClient.addKeyboardHandler(new CefKeyboardHandlerAdapter() {
                    @Override
                    public boolean onKeyEvent(CefBrowser browser, CefKeyboardHandler.CefKeyEvent event) {
                        if (event.windows_key_code == KeyEvent.VK_F12) {
                            if (event.type == CefKeyboardHandler.CefKeyEvent.EventType.KEYEVENT_RAWKEYDOWN) {
                                if (devToolsUI == null) {
                                    devToolsUI = cefBrowser.getDevTools().getUIComponent();
                                    if (getWidth() >= 800) {
                                        devToolsUI.setPreferredSize(new Dimension(getWidth(), 300));
                                    } else {
                                        devToolsUI.setPreferredSize(new Dimension(800, 300));
                                    }
                                    getContentPane().add(devToolsUI, BorderLayout.SOUTH);
                                    pack();
                                    setLocationRelativeTo(null);
                                } else {
                                    devToolsUI.setVisible(!devToolsUI.isVisible());
                                }
                                validate();
                                getContentPane().repaint();
                            }
                            return true;
                        } else if (event.windows_key_code == KeyEvent.VK_F5) {
                            if (!cefBrowser.isLoading()) {
                                cefBrowser.reloadIgnoreCache();
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onLoadError(CefBrowser cb, CefFrame cf, ErrorCode ec, String string, String string1) {
            }
        });

        browserUI.setPreferredSize(new Dimension(width, height));
        if (isFullScreen) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        // Try to set the image icon for the JFrame.
        if (iconFile != null && iconFile.exists()) {
            try {
                setIconImage(ImageIO.read(iconFile));
            } catch (IOException ex) {
                Logger.getLogger(EmbeddedBrowser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        getContentPane().add(browserUI, BorderLayout.CENTER);
        setTitle(title);
        validate();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setVisible(true);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public CefApp getCefApp() {
        return cefApp;
    }

    public CefClient getCefClient() {
        return cefClient;
    }

    public CefBrowser getCefBrowser() {
        return cefBrowser;
    }

    public Component getBrowserUI() {
        return browserUI;
    }

    public void display(String url, String projectName, int newWidth, int newHeight, boolean isFullScreen,
            File iconFile) {
        getCefBrowser().loadURL(url);
        setTitle(projectName);
        getBrowserUI().setPreferredSize(new Dimension(newWidth, newHeight));
        setSize(new Dimension(newWidth, newHeight));
        if (isFullScreen) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        // Try to set the image icon for the JFrame.
        if (iconFile != null && iconFile.exists()) {
            try {
                setIconImage(ImageIO.read(iconFile));
            } catch (IOException ex) {
                Logger.getLogger(EmbeddedBrowser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        revalidate();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        toFront();
        requestFocus();
    }

    public void conceal() {
        setVisible(false);
        cefBrowser.loadURL("http://localhost");
    }

    public void stop() {
        if (cefApp != null) {
            cefApp.dispose();
        }
        // dispose(); // This crashes everything.
    }

    public static void main(String[] args) throws Exception {
        javax.swing.SwingUtilities.invokeLater(() -> {
            EmbeddedBrowser test = new EmbeddedBrowser("Test", "http://localhost:8080/index.html", OS.isLinux(), false,
                    640, 480, false, null);
            test.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    test.stop();
                }
            });
        });
    }
}
