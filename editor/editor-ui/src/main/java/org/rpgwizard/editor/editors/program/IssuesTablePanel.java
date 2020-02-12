/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.program;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class IssuesTablePanel extends JPanel {

    private final int referenceHeight;
    private final JTable table;

    public IssuesTablePanel(int height) {
        super(new BorderLayout());

        this.referenceHeight = height;

        table = new JTable(new IssuesTableModel());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        GuiHelper.setJTableColumnsWidth(table, 768, 2, 96, 2);

        add(new JScrollPane(table));
    }

    public void addNotices(List<ParserNotice> notices) {
        IssuesTableModel model = (IssuesTableModel) table.getModel();
        model.setNotices(notices);
    }

    @Override
    public Dimension getPreferredSize() {
        super.getPreferredSize();
        return calculateDimensions(super.getPreferredSize().width);
    }

    @Override
    public Dimension getMaximumSize() {
        return calculateDimensions(super.getMaximumSize().width);
    }

    @Override
    public Dimension getMinimumSize() {
        return calculateDimensions(super.getMinimumSize().width);
    }

    private Dimension calculateDimensions(int width) {
        return new Dimension(width, referenceHeight);
    }

}
