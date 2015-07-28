package io.tinwhiskers.firesight.gui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class InputPanel extends JPanel {
    private final Main main;
    
    private DirectoryInputPanel directoryInputPanel;
    private CameraInputPanel cameraInputPanel;
    private JTabbedPane inputTabbedPane;
    
    public InputPanel(Main main) {
        this.main = main;
        
        directoryInputPanel = new DirectoryInputPanel(main);
        setLayout(new BorderLayout(0, 0));
        
        inputTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(inputTabbedPane, BorderLayout.CENTER);
        inputTabbedPane.addTab("Directory", directoryInputPanel);
        
        cameraInputPanel = new CameraInputPanel(main);
        inputTabbedPane.addTab("Camera", cameraInputPanel);
    }
    
    public File getInputFile() {
        if (inputTabbedPane.getSelectedIndex() == 0) {
            return directoryInputPanel.getInputFile();
        }
        else {
            return cameraInputPanel.getInputFile();
        }
    }
}
