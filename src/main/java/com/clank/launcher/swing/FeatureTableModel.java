/*
 * SK's Minecraft Launcher
 * Copyright (C) 2010-2014 Albert Pham <http://www.sk89q.com> and contributors
 * Please see LICENSE.txt for license information.
 */

package com.clank.launcher.swing;

import com.clank.launcher.model.modpack.Feature;

import javax.swing.table.AbstractTableModel;
import java.util.List;

import static com.clank.launcher.util.SharedLocale._;

public class FeatureTableModel extends AbstractTableModel {

    private final List<Feature> features;

    public FeatureTableModel(List<Feature> features) {
        this.features = features;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 1:
                return _("features.nameColumn");
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Boolean.class;
            case 1:
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                features.get(rowIndex).setSelected((boolean) (Boolean) value);
                break;
            case 1:
            default:
                break;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return true;
            case 1:
                return false;
            default:
                return false;
        }
    }

    @Override
    public int getRowCount() {
        return features.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return features.get(rowIndex).isSelected();
            case 1:
                Feature feature = features.get(rowIndex);
                return "<html>" + SwingHelper.htmlEscape(feature.getName()) + getAddendum(feature) + "</html>";
            default:
                return null;
        }
    }

    private String getAddendum(Feature feature) {
        if (feature.getRecommendation() == null) {
            return "";
        }
        switch (feature.getRecommendation()) {
            case STARRED:
                return " <span style=\"color: #3758DB\">" + _("features.starred") + "</span>";
            case AVOID:
                return " <span style=\"color: red\">" + _("features.avoid") + "</span>";
            default:
                return "";
        }
    }

}
