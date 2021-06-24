/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map;

import javax.swing.table.AbstractTableModel;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.assets.listeners.MapChangeListener;
import org.rpgwizard.common.assets.events.MapChangedEvent;

/**
 * We want to update the map model here, not the view. After updating the model will fire an event to notify the view of
 * the change.
 *
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public class MapLayersTableModel extends AbstractTableModel implements MapChangeListener {

    private AbstractMapView mapView;

    private static final String[] COLUMNS = { "Locked", "Show", "Layer Name" };

    /**
     *
     */
    public MapLayersTableModel() {

    }

    /**
     *
     * @param map
     */
    public MapLayersTableModel(AbstractMapView map) {
        mapView = map;
        mapView.getMap().addMapChangeListener(this);
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

    /**
     *
     *
     * @return
     */
    @Override
    public int getRowCount() {
        if (mapView == null) {
            return 0;
        } else {
            return mapView.getTotalLayers();
        }
    }

    /**
     *
     *
     * @return
     */
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
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
            return Boolean.class;
        case 1:
            return Boolean.class;
        case 2:
            return String.class;
        }

        return null;
    }

    /**
     *
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MapLayerView layerView = mapView.getLayer(getRowCount() - rowIndex - 1);

        if (layerView != null) {
            switch (columnIndex) {
            case 0:
                return null;
            // return layerView.isLocked();
            case 1:
                return layerView.isVisible();
            case 2:
                return layerView.getLayer().getId();
            default:
                return null;
            }
        } else {
            return null;
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
        MapLayerView layer = mapView.getLayer(getRowCount() - rowIndex - 1);

        return !(columnIndex == 0 && layer != null && !layer.isVisible());
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
        // The layerView locking and visibility is solely view related so we
        // don't have to worry about the model there, but the name is
        // linked to the model map in the background.
        MapLayerView layerView = mapView.getLayer(getRowCount() - rowIndex - 1);

        if (layerView != null) {
            switch (columnIndex) {
            // layer.setLocked((Boolean)value);
            case 0:
                break;
            case 1:
                layerView.setVisibility((Boolean) value);
                break;
            case 2:
                // View need to do this using the map models layerTitles
                // the model will then need to update the view, not the
                // other
                // way around.
                layerView.getLayer().setId(value.toString());
                break;
            default:
                break;
            }

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mapChanged(MapChangedEvent e) {
        // Do not respond to this, or the opacity slider will not work!
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mapLayerAdded(MapChangedEvent e) {
        fireTableDataChanged();
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mapLayerMovedUp(MapChangedEvent e) {
        // fireTableRowsUpdated(firstRow, lastRow);
        fireTableDataChanged();
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mapLayerMovedDown(MapChangedEvent e) {
        // fireTableRowsUpdated(firstRow, lastRow);
        fireTableDataChanged();
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mapLayerCloned(MapChangedEvent e) {
        fireTableDataChanged();
    }

    /**
     *
     *
     * @param e
     */
    @Override
    public void mapLayerDeleted(MapChangedEvent e) {
        fireTableDataChanged();
    }

    @Override
    public void mapSpriteAdded(MapChangedEvent e) {
    }

    @Override
    public void mapSpriteMoved(MapChangedEvent e) {
    }

    @Override
    public void mapSpriteRemoved(MapChangedEvent e) {
    }

    @Override
    public void mapImageAdded(MapChangedEvent e) {
    }

    @Override
    public void mapImageMoved(MapChangedEvent e) {
    }

    @Override
    public void mapImageRemoved(MapChangedEvent e) {
    }

}
