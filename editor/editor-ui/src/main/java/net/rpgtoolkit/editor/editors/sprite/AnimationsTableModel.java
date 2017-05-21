/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.sprite;

import javax.swing.table.AbstractTableModel;
import net.rpgtoolkit.common.assets.AbstractSprite;
import net.rpgtoolkit.common.assets.events.SpriteChangedEvent;
import net.rpgtoolkit.common.assets.listeners.SpriteChangeListener;

/**
 *
 * @author Joshua Michael Daly
 */
public class AnimationsTableModel extends AbstractTableModel
		implements
			SpriteChangeListener {

	private final AbstractSprite sprite;

	private static final String[] COLUMNS = {"Name", "Animation"};

	public AnimationsTableModel(AbstractSprite sprite) {
		this.sprite = sprite;
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
			case 0 :
				return String.class;
			case 1 :
				return String.class;
		}

		return null;
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
		return (sprite.getAnimations().size());
	}

	@Override
	public int getColumnCount() {
		return COLUMNS.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0 :
				return sprite.getAnimations().keySet().toArray()[rowIndex];
			case 1 :
				return sprite.getAnimations().values().toArray()[rowIndex];
			default :
				return "NOT SUPPORTED";
		}
	}

	/**
	 *
	 *
	 * @param value
	 * @param rowIndex
	 * @param columnIndex
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		fireTableCellUpdated(rowIndex, columnIndex);
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

	@Override
	public void spriteChanged(SpriteChangedEvent e) {
	}

	@Override
	public void spriteAnimationAdded(SpriteChangedEvent e) {
		fireTableDataChanged();
	}

	@Override
	public void spriteAnimationUpdated(SpriteChangedEvent e) {
		fireTableDataChanged();
	}

	@Override
	public void spriteAnimationRemoved(SpriteChangedEvent e) {
		fireTableDataChanged();
	}

}
