package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class OutputPanel extends JPanel {
    private final Main main;
    
    private JList<File> outputImagesList;
    private Map<Stage, File> outputFiles;
    private DefaultListModel<File> outputImagesListModel = new DefaultListModel<File>();
        
    public OutputPanel(Main main) {
        this.main = main;
        
        setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane_2 = new JScrollPane();
        add(scrollPane_2, BorderLayout.CENTER);
        
        outputImagesList = new JList<File>(outputImagesListModel);
        outputImagesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        outputImagesList.setCellRenderer(new IconListRenderer(outputImagesList));
        scrollPane_2.setViewportView(outputImagesList);
    }
    
    public void setOutputFiles(Map<Stage, File> output) {
        this.outputFiles = output;
        outputImagesListModel.clear();
        if (output == null) {
            return;
        }
        for (File file : output.values()) {
            outputImagesListModel.addElement(file);
        }
    }
    
    public void highlightOutput(Stage stage) {
        if (outputFiles == null) {
            return;
        }
        File file = outputFiles.get(stage);
        int selectedIndex = outputImagesListModel.indexOf(file);
        outputImagesList.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
        outputImagesList.ensureIndexIsVisible(outputImagesList.getSelectedIndex());
    }
}
