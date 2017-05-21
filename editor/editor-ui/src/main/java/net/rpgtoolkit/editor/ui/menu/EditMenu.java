/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import net.rpgtoolkit.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class EditMenu extends JMenu {

  private JMenuItem undoMenuItem;
  private JMenuItem redoMenuItem;
  private JMenuItem cutMenuItem;
  private JMenuItem copyMenuItem;
  private JMenuItem pasteMenuItem;
  private JMenuItem selectAllMenuItem;
  private JMenuItem commentMenuItem;
  private JMenuItem findMenuItem;
  private JMenuItem quickReplaceMenuItem;

  public EditMenu() {
    super("Edit");

    setMnemonic(KeyEvent.VK_E);

    configureUndoMenuItem();
    configureRedoMenuItem();
    configureCutMenuItem();
    configureCopyMenuItem();
    configurePasteMenuItem();
    configureSelectAllMenuItem();
    configureCommentMenuItem();
    configureFindMenuItem();
    configureQuickReplaceMenuItem();

    add(undoMenuItem);
    add(redoMenuItem);
    add(new JSeparator());
    add(cutMenuItem);
    add(copyMenuItem);
    add(pasteMenuItem);
    add(new JSeparator());
    add(selectAllMenuItem);
    add(commentMenuItem);
    add(new JSeparator());
    add(findMenuItem);
    add(quickReplaceMenuItem);
  }

  public void configureUndoMenuItem() {
    undoMenuItem = new JMenuItem("Undo");
    undoMenuItem.setIcon(Icons.getSmallIcon("undo"));
    undoMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
    undoMenuItem.setMnemonic(KeyEvent.VK_U);

    undoMenuItem.setEnabled(false);
  }

  public void configureRedoMenuItem() {
    redoMenuItem = new JMenuItem("Redo");
    redoMenuItem.setIcon(Icons.getSmallIcon("redo"));
    redoMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
    redoMenuItem.setMnemonic(KeyEvent.VK_R);

    redoMenuItem.setEnabled(false);
  }

  public void configureCutMenuItem() {
    cutMenuItem = new JMenuItem("Cut");
    cutMenuItem.setIcon(Icons.getSmallIcon("cut"));
    cutMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
    cutMenuItem.setMnemonic(KeyEvent.VK_T);

    cutMenuItem.setEnabled(false);
  }

  public void configureCopyMenuItem() {
    copyMenuItem = new JMenuItem("Copy");
    copyMenuItem.setIcon(Icons.getSmallIcon("copy"));
    copyMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
    copyMenuItem.setMnemonic(KeyEvent.VK_C);

    copyMenuItem.setEnabled(false);
  }

  public void configurePasteMenuItem() {
    pasteMenuItem = new JMenuItem("Paste");
    pasteMenuItem.setIcon(Icons.getSmallIcon("paste"));
    pasteMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
    pasteMenuItem.setMnemonic(KeyEvent.VK_P);

    pasteMenuItem.setEnabled(false);
  }

  public void configureSelectAllMenuItem() {
    selectAllMenuItem = new JMenuItem("Select All");
    selectAllMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
    selectAllMenuItem.setMnemonic(KeyEvent.VK_A);

    selectAllMenuItem.setEnabled(false);
  }

  public void configureCommentMenuItem() {
    commentMenuItem = new JMenuItem("Un/Comment Selected");
    commentMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK
                    + ActionEvent.SHIFT_MASK));
    commentMenuItem.setMnemonic(KeyEvent.VK_M);

    commentMenuItem.setEnabled(false);
  }

  public void configureFindMenuItem() {
    findMenuItem = new JMenuItem("Quick Find");
    findMenuItem.setIcon(Icons.getSmallIcon("find"));
    findMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
    findMenuItem.setMnemonic(KeyEvent.VK_F);

    findMenuItem.setEnabled(false);
  }

  public void configureQuickReplaceMenuItem() {
    quickReplaceMenuItem = new JMenuItem("Quick Replace");
    quickReplaceMenuItem.setIcon(Icons.getSmallIcon("replace"));
    quickReplaceMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
    quickReplaceMenuItem.setMnemonic(KeyEvent.VK_R);

    quickReplaceMenuItem.setEnabled(false);
  }
  
}
