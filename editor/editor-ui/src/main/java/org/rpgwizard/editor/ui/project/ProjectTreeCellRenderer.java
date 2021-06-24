/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.project;

import java.awt.Component;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.apache.commons.io.FilenameUtils;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public class ProjectTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean isLeaf, int row, boolean focused) {
        Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, row, focused);

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            // Get the last element of the node path.
            String[] parts = getText().split(File.separator.equals("\\") ? "\\\\" : "/");
            String title = parts[parts.length - 1];
            setText(title);

            // Set the icon if possible.
            ImageIcon icon;
            File file = new File(node.toString());
            if (file.exists() && file.isDirectory()) {
                icon = Icons.getDefaultIcon("folder");
            } else {
                String extension = FilenameUtils.getExtension(node.toString());
                icon = Icons.getDefaultIcon(extension.toLowerCase());
            }

            if (icon != null) {
                setIcon(icon);
            }
        }

        return component;
    }

}
