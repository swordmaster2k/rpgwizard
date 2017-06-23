/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.listeners;

import java.io.File;
import java.util.Collection;
import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Joshua Michael Daly
 */
public class PopupListFilesListener implements PopupMenuListener {

	private final File[] rootDirectories;
	private final String[] extension;
	private final boolean recursive;
	private final JComboBox comboBox;

	public PopupListFilesListener(File[] directories, String[] exts,
			boolean isRecursive, JComboBox model) {
		rootDirectories = directories;
		extension = exts;
		recursive = isRecursive;
		comboBox = model;

		populate();
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		Object previouslySelected = comboBox.getSelectedItem();
		comboBox.removeAllItems();
		comboBox.addItem("");
		populate();
		comboBox.setSelectedItem(previouslySelected);
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	private void populate() {
		for (File rootDirectory : rootDirectories) {
			Collection<File> files = FileUtils.listFiles(rootDirectory,
					extension, recursive);

			for (File file : files) {
				String path = file.getAbsolutePath();
				path = path.replace(rootDirectory.getAbsolutePath()
						+ File.separator, "");

				comboBox.addItem(FilenameUtils.separatorsToUnix(path));
			}
		}

	}

}
