/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.ui;

import java.io.File;
import javax.swing.filechooser.FileSystemView;

/**
 * A FileSystemView class that limits the file selections to a single root.
 *
 * When used with the JFileChooser component the user will only be able to
 * traverse the directories contained within the specified root fill.
 *
 * The "Look In" combo box will only display the specified root.
 *
 * The "Up One Level" button will be disable when at the root.
 *
 * Taken from: http://www.camick.com/java/source/SingleRootFileSystemView.java
 */
public class SingleRootFileSystemView extends FileSystemView {

	private final File root;
	private final File[] roots;

	public SingleRootFileSystemView(File root) {
		super();

		this.root = root;
		roots = new File[]{root};
	}

	@Override
	public File createNewFolder(File containingDir) {
		File folder = new File(containingDir, "New Folder");
		folder.mkdir();

		return folder;
	}

	@Override
	public File getDefaultDirectory() {
		return root;
	}

	@Override
	public File getHomeDirectory() {
		return root;
	}

	@Override
	public File[] getRoots() {
		return roots;
	}

}
