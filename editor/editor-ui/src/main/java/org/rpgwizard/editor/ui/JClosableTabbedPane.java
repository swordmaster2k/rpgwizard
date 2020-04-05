/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import com.formdev.flatlaf.icons.FlatInternalFrameCloseIcon;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 * An extension of the JTabbedPane which automatically adds a close button.
 *
 * Originally inspired by: https://gist.github.com/6dc/0c8926f85d701a869bb2
 *
 * @author Joshua Michael Daly
 */
public class JClosableTabbedPane extends JTabbedPane {

    public JClosableTabbedPane() {
        super();
    }

    @Override
    public void addTab(String title, Icon icon, Component component, String tip) {
        super.addTab(title, icon, component, tip);
        int count = getTabCount() - 1;
        setTabComponentAt(count, new CloseButtonTab(component, title, icon));
    }

    @Override
    public void addTab(String title, Icon icon, Component component) {
        addTab(title, icon, component, null);
    }

    @Override
    public void addTab(String title, Component component) {
        addTab(title, null, component);
    }

    public final class CloseButtonTab extends JPanel {

        public CloseButtonTab(final Component tab, String title, Icon icon) {
            JLabel jLabel = new JLabel(title);
            jLabel.setIcon(icon);

            JButton closeButton = new JButton(new FlatInternalFrameCloseIcon());
            closeButton.addMouseListener(new CloseListener(tab));
            closeButton.setPreferredSize(new Dimension(16, 16));
            closeButton.setMaximumSize(new Dimension(16, 16));
            closeButton.setContentAreaFilled(false);
            closeButton.setBorder(null);

            BoxLayout boxlayout = new BoxLayout(this, BoxLayout.X_AXIS);
            setLayout(boxlayout);

            add(jLabel);
            add(Box.createRigidArea(new Dimension(7, 0)));
            add(closeButton);

            setBorder(new EmptyBorder(3, 3, 3, 3));

            setOpaque(false);
        }

    }

    public final class CloseListener extends MouseAdapter {

        private final Component tab;

        public CloseListener(Component tab) {
            this.tab = tab;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof JButton) {
                JButton clickedButton = (JButton) e.getSource();
                JTabbedPane tabbedPane = (JTabbedPane) clickedButton.getParent().getParent().getParent();
                tabbedPane.remove(tab);
            }
        }

    }
}
