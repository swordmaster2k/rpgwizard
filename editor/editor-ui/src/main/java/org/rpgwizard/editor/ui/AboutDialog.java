/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import org.rpgwizard.editor.properties.EditorProperties;
import org.rpgwizard.editor.properties.EditorProperty;

/**
 *
 * @author Joshua Michael Daly
 */
public final class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent) {
        super(parent, "About RPGWizard", true);

        JLabel titleLabel = new JLabel(EditorProperties.getProperty(EditorProperty.EDITOR_UI_TITLE));
        titleLabel.setFont(new Font(titleLabel.getName(), Font.BOLD, 16));

        String disclaimer = "RPGWizard is an open source project, and is supported by its community. It owes its heritage to the RPGToolkit, from which it was originally forked."
                + "\n\n" + "The RPGWizard team is lead by Joshua Michael Daly." + "\n\n" + "Special thanks to:" + "\n"
                + " - Grindalf, for supplying the demo game assets." + "\n\n" + "Some icons by:" + "\n"
                + " - Yusuke Kamiyamane. Licensed under a Creative Commons Attribution 3.0 License." + "\n"
                + " - Icons8. Licensed under a Creative Commons Attribution-NoDerivs 3.0 Unported." + "\n\n"
                + "Copyright 2016-2020 RPGWizard. All rights reserved." + "\n\n"
                + "Covered Software is provided under this License on "
                + "an “as is” basis, without warranty of any kind, either "
                + "expressed, implied, or statutory, including, without "
                + "limitation, warranties that the Covered Software is free of "
                + "defects, merchantable, fit for a particular purpose or non-"
                + "infringing. The entire risk as to the quality and performance "
                + "of the Covered Software is with You. Should any Covered "
                + "Software prove defective in any respect, You (not any Contributor) "
                + "assume the cost of any necessary servicing, repair, or correction. "
                + "This disclaimer of warranty constitutes an essential part "
                + "of this License. No use of any Covered Software is authorized "
                + "under this License except under this disclaimer.";

        JTextArea disclaimerArea = new JTextArea(22, 50);
        disclaimerArea.setText(disclaimer);
        disclaimerArea.setWrapStyleWord(true);
        disclaimerArea.setLineWrap(true);
        disclaimerArea.setOpaque(false);
        disclaimerArea.setEditable(false);
        disclaimerArea.setFocusable(false);
        disclaimerArea.setBackground(UIManager.getColor("Label.background"));
        disclaimerArea.setFont(UIManager.getFont("Label.font"));
        disclaimerArea.setBorder(UIManager.getBorder("Label.border"));

        Box box = Box.createVerticalBox();
        box.add(Box.createGlue());
        box.add(titleLabel);
        box.add(Box.createVerticalStrut(10));
        box.add(new JLabel("Copyright 2016-2020 RPGWizard. All rights reserved."));
        box.add(Box.createVerticalStrut(10));
        box.add(disclaimerArea);
        box.add(Box.createGlue());

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel);

        JPanel textPanel = new JPanel();
        textPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        textPanel.add(disclaimerArea);

        JPanel okPanel = new JPanel();
        JButton ok = new JButton("Ok");
        okPanel.add(ok);

        getContentPane().add(titlePanel, BorderLayout.NORTH);
        getContentPane().add(textPanel, BorderLayout.CENTER);
        getContentPane().add(okPanel, BorderLayout.SOUTH);

        ok.addActionListener((ActionEvent evt) -> {
            setVisible(false);
        });

        pack();
    }

    public static void main(String[] args) {
        JDialog dialog = new AboutDialog(new JFrame());
        dialog.setVisible(true);
    }

}
