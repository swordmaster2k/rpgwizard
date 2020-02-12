/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.program;

import java.util.List;
import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;
import org.fife.ui.rsyntaxtextarea.parser.ParserNotice;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
public class IssuesTableModel extends AbstractTableModel {

    private static final String[] COLUMNS = { "Type", "Message", "Line" };

    private List<ParserNotice> notices;

    public IssuesTableModel() {
        notices = List.of();
    }

    public List<ParserNotice> getNotices() {
        return notices;
    }

    public void setNotices(List<ParserNotice> notices) {
        this.notices = notices;
        fireTableDataChanged();
    }

    /**
     *
     *
     * @param column
     * @return
     */
    @Override
    public Class getColumnClass(int column) {
        switch (column) {
        case 0:
            return Icon.class;
        case 1:
            return String.class;
        case 2:
            return String.class;
        default:
            return String.class;
        }
    }

    /**
     *
     *
     * @param column
     * @return
     */
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public int getRowCount() {
        return notices.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0:
            switch (notices.get(rowIndex).getLevel()) {
            case ERROR:
                return Icons.getIcon("error");
            case WARNING:
                return Icons.getIcon("warning");
            default:
                return Icons.getIcon("information");
            }
        case 1:
            return notices.get(rowIndex).getMessage();
        case 2:
            return notices.get(rowIndex).getLine();
        default:
            return "NOT SUPPORTED";
        }
    }

    /**
     *
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Don't allow direct editing of cells for simplicity.
        return false;
    }

}
