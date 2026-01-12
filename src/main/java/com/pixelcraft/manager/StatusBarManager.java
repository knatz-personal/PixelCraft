package com.pixelcraft.manager;

import com.pixelcraft.util.IconUtil;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.util.Pair;

public class StatusBarManager {

    private final Label lblImageSize;
    private final Label lblFileSize;
    private final Label lblPosition;
    private final Label lblMode;
    private final ComboBox<Pair<String, Double>> cmbZoomPresets;
    private ListCell<Pair<String, Double>> zoomButtonCell;

    public StatusBarManager(Label lblImageSize, Label lblFileSize, Label lblPosition, Label lblMode, ComboBox<Pair<String, Double>> cmbZoomPresets) {
        this.lblImageSize = lblImageSize;
        this.lblFileSize = lblFileSize;
        this.lblPosition = lblPosition;
        this.lblMode = lblMode;
        this.cmbZoomPresets = cmbZoomPresets;
        initZoomPresets();
        //set icons
        lblImageSize.setGraphic(IconUtil.createIconByName("image"));
        lblFileSize.setGraphic(IconUtil.createIconByName("save"));
        lblPosition.setGraphic(IconUtil.createIconByName("arrow_selector_tool"));
    }

    public void updateFileSize(long sizeInBytes) {
        String denomination;
        double displaySize;

        if (sizeInBytes < 1024) {
            displaySize = sizeInBytes;
            denomination = "bytes";
        } else if (sizeInBytes < 1024 * 1024) {
            displaySize = sizeInBytes / 1024.0;
            denomination = "KB";
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            displaySize = sizeInBytes / (1024.0 * 1024.0);
            denomination = "MB";
        } else {
            displaySize = sizeInBytes / (1024.0 * 1024.0 * 1024.0);
            denomination = "GB";
        }
        String size = String.format("%.2f %s", displaySize, denomination);
        lblFileSize.setText(String.format("Size: %8s", size));
    }

    public void updateImageSize(double width, double height) {
        lblImageSize.setText(String.format("%.0f x %.0f px", width, height));
    }

    public void updatePosition(double x, double y) {
        String size = String.format("(%.0f, %.0f)", x, y);
        lblPosition.setText(String.format("%15s", size));
    }

    public void clearPosition() {
        lblPosition.setText(String.format("%-15s", ""));
    }

    public void updateMode(String mode) {
        lblMode.setText("Mode: " + mode);
    }

    public void updateZoom(double zoomLevel) {
        if (zoomButtonCell != null) {
            zoomButtonCell.setText(String.format("%.0f%%", zoomLevel * 100));
        }

        // Select matching preset if exists
        for (Pair<String, Double> preset : cmbZoomPresets.getItems()) {
            if (Math.abs(preset.getValue() - zoomLevel) < 0.001) {
                cmbZoomPresets.getSelectionModel().select(preset);
                return;
            }
        }
        cmbZoomPresets.getSelectionModel().clearSelection();
    }

    private void initZoomPresets() {
        cmbZoomPresets.setItems(FXCollections.observableArrayList(
                java.util.Arrays.asList(
                        new Pair<>("10%", 0.10),
                        new Pair<>("25%", 0.25),
                        new Pair<>("50%", 0.50),
                        new Pair<>("75%", 0.75),
                        new Pair<>("100%", 1.0),
                        new Pair<>("200%", 2.0),
                        new Pair<>("300%", 3.0),
                        new Pair<>("400%", 4.0),
                        new Pair<>("500%", 5.0),
                        new Pair<>("600%", 6.0),
                        new Pair<>("700%", 7.0),
                        new Pair<>("800%", 8.0),
                        new Pair<>("900%", 9.0),
                        new Pair<>("1000%", 10.0)
                )
        ));

        cmbZoomPresets.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Double> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getKey());
            }
        });

        zoomButtonCell = new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, Double> item, boolean empty) {
                super.updateItem(item, empty);
                setText("100%"); // Will be updated by updateZoom()
            }
        };
        cmbZoomPresets.setButtonCell(zoomButtonCell);
    }
}
