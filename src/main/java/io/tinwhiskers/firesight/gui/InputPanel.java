package io.tinwhiskers.firesight.gui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class InputPanel extends JPanel {
    private final Main main;
    
    private DirectoryInputPanel directoryInputPanel;
    
    public InputPanel(Main main) {
        this.main = main;
        
        directoryInputPanel = new DirectoryInputPanel(main);
        setLayout(new BorderLayout(0, 0));
        
        JTabbedPane inputTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        add(inputTabbedPane, BorderLayout.CENTER);
        
        inputTabbedPane.add("Directory", directoryInputPanel);
        
    }
    
    public File getInputFile() {
        return directoryInputPanel.getInputFile();
    }
}
