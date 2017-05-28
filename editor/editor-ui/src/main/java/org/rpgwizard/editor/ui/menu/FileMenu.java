/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.menu;

import org.rpgwizard.editor.MainWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import org.rpgwizard.editor.ui.actions.ExitAction;
import org.rpgwizard.editor.ui.actions.NewProjectAction;
import org.rpgwizard.editor.ui.actions.OpenFileAction;
import org.rpgwizard.editor.ui.actions.OpenProjectAction;
import org.rpgwizard.editor.ui.actions.SaveAction;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public final class FileMenu extends JMenu implements ActionListener {

	private JMenu newMenu;
	private JMenuItem newProjectMenuItem;
	private JMenuItem newAnimationMenuItem;
	private JMenuItem newBoardMenuItem;
	private JMenuItem newCharacterMenuItem;
	private JMenuItem newEnemyMenuItem;
	private JMenuItem newNPCMenuItem;
	private JMenuItem newProgramMenuItem;
	private JMenuItem newTilesetMenuItem;

	private JMenu openMenu;
	private JMenuItem openProjectMenuItem;
	private JMenuItem openFileMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem saveAsMenuItem;
	private JMenuItem saveAllMenuItem;
	private JMenuItem exitMenuItem;

	/**
     *
     */
	public FileMenu() {
		super("File");

		setMnemonic(KeyEvent.VK_F);

		configureFileMenu();

		add(newProjectMenuItem);
		add(newMenu);
		add(openProjectMenuItem);
		add(openMenu);
		add(new JSeparator());
		add(saveMenuItem);
		add(saveAsMenuItem);
		add(saveAllMenuItem);
		add(new JSeparator());
		add(exitMenuItem);
	}

	public JMenu getNewMenu() {
		return newMenu;
	}

	public JMenuItem getNewProjectMenuItem() {
		return newProjectMenuItem;
	}

	public JMenu getOpenMenu() {
		return openMenu;
	}

	public JMenuItem getOpenProjectMenuItem() {
		return openProjectMenuItem;
	}

	public JMenuItem getOpenFileMenuItem() {
		return openFileMenuItem;
	}

	public JMenuItem getSaveMenuItem() {
		return saveMenuItem;
	}

	public JMenuItem getSaveAsMenuItem() {
		return saveAsMenuItem;
	}

	public JMenuItem getSaveAllMenuItem() {
		return saveAllMenuItem;
	}

	public JMenuItem getExitMenuItem() {
		return exitMenuItem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == newProjectMenuItem) {

		} else if (e.getSource() == newAnimationMenuItem) {
			MainWindow.getInstance().createNewAnimation();
		} else if (e.getSource() == newBoardMenuItem) {
			MainWindow.getInstance().createNewBoard();
		} else if (e.getSource() == newCharacterMenuItem) {
			MainWindow.getInstance().createNewCharacter();
		} else if (e.getSource() == newEnemyMenuItem) {
			MainWindow.getInstance().createNewEnemy();
		} else if (e.getSource() == newNPCMenuItem) {
			MainWindow.getInstance().createNewNPC();
		} else if (e.getSource() == newProgramMenuItem) {
			MainWindow.getInstance().createNewProgram();
		} else if (e.getSource() == newTilesetMenuItem) {
			MainWindow.getInstance().createNewTileset();
		}
	}

	/**
	 * Enable all the menu items after a project has been opened.
	 */
	public void doEnableItems() {
		openFileMenuItem.setEnabled(true);
		newAnimationMenuItem.setEnabled(true);
		newBoardMenuItem.setEnabled(true);
		newCharacterMenuItem.setEnabled(true);
		newEnemyMenuItem.setEnabled(true);
		newNPCMenuItem.setEnabled(true);
		newProgramMenuItem.setEnabled(true);
		newTilesetMenuItem.setEnabled(true);
		saveMenuItem.setEnabled(true);
	}

	/**
     *
     */
	private void configureFileMenu() {
		configureNewSubMenu();
		configureOpenSubMenu();
		configureSaveMenuItem();
		configureSaveAsMenuItem();
		configureSaveAllMenuItem();
		configureExitMenuItem();
	}

	/**
     *
     */
	private void configureNewSubMenu() {
		configureNewProjectMenuItem();
		configureNewAnimationMenuItem();
		configureNewBoardMenuItem();
		configureNewCharacterMenuItem();
		configureNewEnemyMenuItem();
		configureNewNPCMenuItem();
		configureNewProgramMenuItem();
		configureNewTilesetMenuItem();

		newMenu = new JMenu("New");
		newMenu.setEnabled(true);
		newMenu.add(newAnimationMenuItem);
		newMenu.add(newBoardMenuItem);
		newMenu.add(newCharacterMenuItem);
		newMenu.add(newEnemyMenuItem);
		newMenu.add(newNPCMenuItem);
		newMenu.add(newProgramMenuItem);
		newMenu.add(newTilesetMenuItem);
	}

	private void configureOpenSubMenu() {
		configureOpenProjectMenuItem();
		configureOpenFileMenuItem();

		openMenu = new JMenu("Open");
		openMenu.add(openFileMenuItem);
	}

	private void configureNewProjectMenuItem() {
		newProjectMenuItem = new JMenuItem("New Project");
		newProjectMenuItem.setAction(new NewProjectAction());
		newProjectMenuItem.setText("New Project");
		newProjectMenuItem.setIcon(Icons.getSmallIcon("new-project"));
		newProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				ActionEvent.CTRL_MASK));
		newProjectMenuItem.setMnemonic(KeyEvent.VK_N);
		newProjectMenuItem.setEnabled(true);
	}

	private void configureNewAnimationMenuItem() {
		newAnimationMenuItem = new JMenuItem("New Animation");
		newAnimationMenuItem.setEnabled(false);
		newAnimationMenuItem.addActionListener(this);
		newAnimationMenuItem.setIcon(Icons.getSmallIcon("new-animation"));
	}

	private void configureNewBoardMenuItem() {
		newBoardMenuItem = new JMenuItem("New Board");
		newBoardMenuItem.setEnabled(false);
		newBoardMenuItem.addActionListener(this);
		newBoardMenuItem.setIcon(Icons.getSmallIcon("new-board"));
	}

	private void configureNewCharacterMenuItem() {
		newCharacterMenuItem = new JMenuItem("New Character");
		newCharacterMenuItem.setEnabled(false);
		newCharacterMenuItem.addActionListener(this);
		newCharacterMenuItem.setIcon(Icons.getSmallIcon("new-character"));
	}

	private void configureNewEnemyMenuItem() {
		newEnemyMenuItem = new JMenuItem("New Enemy");
		newEnemyMenuItem.setEnabled(false);
		newEnemyMenuItem.addActionListener(this);
		newEnemyMenuItem.setIcon(Icons.getSmallIcon("new-enemy"));
	}

	private void configureNewNPCMenuItem() {
		newNPCMenuItem = new JMenuItem("New NPC");
		newNPCMenuItem.setEnabled(false);
		newNPCMenuItem.addActionListener(this);
		newNPCMenuItem.setIcon(Icons.getSmallIcon("new-npc"));
	}

	private void configureOpenProjectMenuItem() {
		openProjectMenuItem = new JMenuItem("Open Project");
		openProjectMenuItem.setAction(new OpenProjectAction());
		openProjectMenuItem.setText("Open Project");
		openProjectMenuItem.setIcon(Icons.getSmallIcon("project"));
		openProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_O, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		openProjectMenuItem.setMnemonic(KeyEvent.VK_T);
	}

	private void configureNewProgramMenuItem() {
		newProgramMenuItem = new JMenuItem("New Program");
		newProgramMenuItem.setEnabled(false);
		newProgramMenuItem.addActionListener(this);
		newProgramMenuItem.setIcon(Icons.getSmallIcon("new-program"));
	}

	private void configureNewTilesetMenuItem() {
		newTilesetMenuItem = new JMenuItem("New Tileset");
		newTilesetMenuItem.setEnabled(false);
		newTilesetMenuItem.addActionListener(this);
		newTilesetMenuItem.setIcon(Icons.getSmallIcon("new-tile"));
	}

	private void configureOpenFileMenuItem() {
		openFileMenuItem = new JMenuItem();
		openFileMenuItem.setAction(new OpenFileAction());
		openFileMenuItem.setText("Open File");
		openFileMenuItem.setEnabled(false);
		openFileMenuItem.setIcon(Icons.getSmallIcon("open"));
		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				ActionEvent.CTRL_MASK));
		openFileMenuItem.setMnemonic(KeyEvent.VK_O);
	}

	private void configureSaveMenuItem() {
		saveMenuItem = new JMenuItem();
		saveMenuItem.setAction(new SaveAction());
		saveMenuItem.setText("Save");
		saveMenuItem.setIcon(Icons.getSmallIcon("save"));
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK));
		saveMenuItem.setMnemonic(KeyEvent.VK_N);
		saveMenuItem.setEnabled(false);
	}

	private void configureSaveAsMenuItem() {
		saveAsMenuItem = new JMenuItem("Save As");
		saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		saveAsMenuItem.setEnabled(false);
	}

	private void configureSaveAllMenuItem() {
		saveAllMenuItem = new JMenuItem("Save All");
		saveAllMenuItem.setIcon(Icons.getSmallIcon("save-all"));
		saveAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		saveAllMenuItem.setEnabled(false);
	}

	private void configureExitMenuItem() {
		exitMenuItem = new JMenuItem();
		exitMenuItem.setAction(new ExitAction());
		exitMenuItem.setText("Exit");
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				ActionEvent.ALT_MASK));
	}

}
