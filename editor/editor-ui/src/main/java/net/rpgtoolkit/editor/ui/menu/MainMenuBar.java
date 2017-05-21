/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui.menu;

import net.rpgtoolkit.editor.MainWindow;
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
  private final HelpMenu helpMenu;

  public MainMenuBar(MainWindow menuBarParent) {
    parent = menuBarParent;

    fileMenu = new FileMenu();
    fileMenu.setName("fileMenu");
    
    editMenu = new EditMenu();
    viewMenu = new ViewMenu();
    runMenu = new RunMenu();
    toolsMenu = new ToolsMenu();
    helpMenu = new HelpMenu();

    add(fileMenu);
    add(editMenu);
    add(viewMenu);
    add(runMenu);
    add(toolsMenu);
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

  @Override
  public HelpMenu getHelpMenu() {
    return helpMenu;
  }

  public void enableMenus(boolean enable) {
    fileMenu.doEnableItems();
  }

}
