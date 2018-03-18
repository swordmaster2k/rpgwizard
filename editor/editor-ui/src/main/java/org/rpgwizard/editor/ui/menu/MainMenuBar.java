/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.menu;

import org.rpgwizard.editor.MainWindow;
import javax.swing.JMenuBar;

/**
 *
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class MainMenuBar extends JMenuBar {

    private final MainWindow parent;

    private final FileMenu fileMenu;
    private final EditMenu editMenu;
    private final ViewMenu viewMenu;
    private final RunMenu runMenu;
    private final ToolsMenu toolsMenu;
    private final WindowMenu windowMenu;
    private final HelpMenu helpMenu;

    public MainMenuBar(MainWindow menuBarParent) {
        parent = menuBarParent;

        fileMenu = new FileMenu();
        fileMenu.setName("fileMenu");

        editMenu = new EditMenu();
        viewMenu = new ViewMenu();
        runMenu = new RunMenu();
        toolsMenu = new ToolsMenu();
        windowMenu = new WindowMenu();
        helpMenu = new HelpMenu();

        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(runMenu);
        add(toolsMenu);
        add(windowMenu);
        add(helpMenu);
    }

    public MainWindow getParentWindow() {
        return parent;
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

    public EditMenu getEditMenu() {
        return editMenu;
    }

    public ViewMenu getViewMenu() {
        return viewMenu;
    }

    public RunMenu getRunMenu() {
        return runMenu;
    }

    public ToolsMenu getToolsMenu() {
        return toolsMenu;
    }

    public WindowMenu getWindowMenu() {
        return windowMenu;
    }

    @Override
    public HelpMenu getHelpMenu() {
        return helpMenu;
    }

    public void enableMenus(boolean enable) {
        fileMenu.doEnableItems();
        editMenu.doEnableItems();
    }

}
