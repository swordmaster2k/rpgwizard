/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.panels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.rpgwizard.editor.editors.map.AbstractMapView;
import org.rpgwizard.editor.editors.map.MapLayerView;
import org.rpgwizard.editor.editors.map.MapLayersTableModel;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class LayerPanel extends JPanel implements ChangeListener, ListSelectionListener {

    private AbstractMapView mapView;

    private JSlider opacitySlider;
    private JLabel opacitySliderLabel;
    private JTable layerTable;
    private JScrollPane layerScrollPane;

    private JButton newLayerButton;
    private JButton moveLayerUpButton;
    private JButton moveLayerDownButton;
    private JButton cloneLayerButton;
    private JButton deleteLayerButton;

    private JPanel sliderPanel;
    private JPanel buttonPanel;

    public LayerPanel() {
        init();
    }

    public LayerPanel(AbstractMapView mapView) {
        this.mapView = mapView;
        init();
    }

    public AbstractMapView getMapView() {
        return mapView;
    }

    public void setMapView(AbstractMapView mapView) {
        this.mapView = mapView;
        layerTable.setModel(new MapLayersTableModel(mapView));
        int selectedLayer = mapView.getCurrentSelectedLayer().getLayer().getNumber();
        selectedLayer = Math.abs(selectedLayer - (mapView.getLayerArrayList().size() - 1));
        if (selectedLayer < 0 || layerTable.getRowCount() < selectedLayer) {
            selectedLayer = 0;
        }
        layerTable.changeSelection(selectedLayer, 0, false, false);
        opacitySlider.setValue(100);
    }

    /**
     * TODO: Possibly consider moving this to a dedicated listener class later. For now leave it here for simplicity.
     *
     * Used to keep track of changes on the opacity <code>JSlider</code>. If there is an open map and a layer is
     * selected then the layers opacity will be updated.
     *
     * @param e
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(opacitySlider)) {
            if (mapView != null) {
                if (layerTable.getSelectedRow() > -1 && layerTable.getRowCount() > 0) {
                    mapView.getLayer((mapView.getMap().getLayers().size() - layerTable.getSelectedRow()) - 1)
                            .setOpacity(opacitySlider.getValue() / 100.0f);
                }
            }
        }
    }

    /**
     * TODO: It is possible that in the future other parts of the editor will be interested in layer selection changes.
     *
     * Handles selection changes on the Layer Table, updating the opacity slider with the selected layers current
     * opacity.
     *
     * @param e
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        // If we have changed the selected layer update the position of the
        // opacity slider to the new layers opacity.
        if (mapView != null) {
            if (layerTable.getSelectedRow() > -1) {
                MapLayerView selectedLayer = mapView
                        .getLayer((mapView.getMap().getLayers().size() - layerTable.getSelectedRow()) - 1);

                mapView.setCurrentSelectedLayer(selectedLayer);

                opacitySlider.setValue((int) (selectedLayer.getOpacity() * 100));
            }
        }
    }

    public void updateTable() {
        layerTable.repaint();
    }

    public void clearTable() {
        layerTable.setModel(new MapLayersTableModel());
    }

    private void init() {
        opacitySlider = new JSlider(0, 100, 100);
        opacitySlider.addChangeListener(this);

        opacitySliderLabel = new JLabel("Opacity");
        opacitySliderLabel.setLabelFor(opacitySlider);

        sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
        sliderPanel.add(opacitySliderLabel);
        sliderPanel.add(opacitySlider);
        sliderPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sliderPanel.getPreferredSize().height));

        if (mapView != null) {
            layerTable = new JTable(new MapLayersTableModel(mapView));

            if (mapView.getMap().getLayers().size() > 0) {
                layerTable.setRowSelectionInterval(layerTable.getModel().getRowCount() - 1, 0);
            }
        } else {
            layerTable = new JTable(new MapLayersTableModel());
        }

        layerTable.getColumnModel().getColumn(0).setPreferredWidth(32);
        layerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        layerTable.getSelectionModel().addListSelectionListener(this);

        layerScrollPane = new JScrollPane(layerTable);

        newLayerButton = new JButton();
        newLayerButton.setIcon(Icons.getSmallIcon("new"));
        newLayerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (mapView != null) {
                    mapView.getMap().addLayer();
                    layerTable.setRowSelectionInterval(layerTable.getModel().getRowCount() - 1, 0);
                }
            }
        });

        moveLayerUpButton = new JButton();
        moveLayerUpButton.setIcon(Icons.getSmallIcon("arrow-090"));
        moveLayerUpButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (mapView != null) {
                    if (layerTable.getSelectedRow() > 0) {
                        int selectedIndex = layerTable.getSelectedRow();
                        boolean result = mapView.getMap().moveLayerUp(-(selectedIndex - layerTable.getRowCount() + 1));
                        if (result) {
                            layerTable.setRowSelectionInterval(selectedIndex - 1, selectedIndex - 1);
                        }
                    }
                }
            }
        });

        moveLayerDownButton = new JButton();
        moveLayerDownButton.setIcon(Icons.getSmallIcon("arrow-270"));
        moveLayerDownButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (mapView != null) {
                    if (layerTable.getSelectedRow() < layerTable.getRowCount() - 1) {
                        int selectedIndex = layerTable.getSelectedRow();
                        boolean result = mapView.getMap()
                                .moveLayerDown(-(selectedIndex - layerTable.getRowCount() + 1));

                        if (result) {
                            layerTable.setRowSelectionInterval(selectedIndex + 1, selectedIndex + 1);
                        }
                    }
                }
            }
        });

        cloneLayerButton = new JButton();
        cloneLayerButton.setIcon(Icons.getSmallIcon("copy"));
        cloneLayerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (mapView != null) {
                    if (layerTable.getSelectedRow() > -1) {
                        int selectedIndex = layerTable.getSelectedRow();
                        mapView.getMap().cloneLayer((mapView.getMap().getLayers().size() - selectedIndex) - 1);
                        layerTable.setRowSelectionInterval(selectedIndex, selectedIndex);
                    }
                }
            }
        });

        deleteLayerButton = new JButton();
        deleteLayerButton.setIcon(Icons.getSmallIcon("delete"));
        deleteLayerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (mapView != null) {
                    if (layerTable.getSelectedRow() > -1) {
                        int selectedIndex = layerTable.getSelectedRow();
                        mapView.getMap()
                                .deleteLayer((mapView.getMap().getLayers().size() - layerTable.getSelectedRow()) - 1);

                        if (layerTable.getRowCount() > 0) {
                            if (selectedIndex == 0) {
                                layerTable.setRowSelectionInterval(selectedIndex, selectedIndex);
                            } else {
                                layerTable.setRowSelectionInterval(selectedIndex - 1, selectedIndex - 1);
                            }
                        }
                    }
                }
            }
        });

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.fill = GridBagConstraints.BOTH;
        buttonsConstraints.weightx = 1;
        buttonPanel.add(newLayerButton, buttonsConstraints);
        buttonPanel.add(moveLayerUpButton, buttonsConstraints);
        buttonPanel.add(moveLayerDownButton, buttonsConstraints);
        buttonPanel.add(cloneLayerButton, buttonsConstraints);
        buttonPanel.add(deleteLayerButton, buttonsConstraints);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonPanel.getPreferredSize().height));

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(3, 0, 0, 0);
        constraints.weightx = 1;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0;
        constraints.gridy++;
        add(sliderPanel, constraints);
        constraints.weighty = 1;
        constraints.gridy++;
        add(layerScrollPane, constraints);
        constraints.weighty = 0;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.gridy++;
        add(buttonPanel, constraints);
    }

}
