package io.tinwhiskers.firesight.gui;

import io.tinwhiskers.firesight.gui.Pipeline.Stage;

import java.awt.BorderLayout;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class Main extends JFrame {
    private Ops ops = new Ops();
    private FireSight fireSight = new FireSight(ops);
    private PipelinePanel pipelinePanel = new PipelinePanel(this);
    private DirectoryInputPanel directoryInputPanel = new DirectoryInputPanel(this);
    private OutputPanel outputPanel = new OutputPanel(this);
    private File workingDirectory;
    
    public Main() {
        try {
            workingDirectory = Files.createTempDirectory("firesight-gui").toFile();
        }
        catch (Exception e) {
            throw new Error(e);
        }
        
        JSplitPane splitPane1 = new JSplitPane();
        getContentPane().add(splitPane1, BorderLayout.CENTER);
        
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        
        JSplitPane splitPane2 = new JSplitPane();
        panel.add(splitPane2, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout(0, 0));
        
        JTabbedPane inputTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        inputPanel.add(inputTabbedPane, BorderLayout.CENTER);
        
        inputTabbedPane.add("Directory", directoryInputPanel);

        splitPane1.setLeftComponent(pipelinePanel);
        splitPane1.setRightComponent(panel);
        
        splitPane2.setLeftComponent(inputPanel);
        splitPane2.setRightComponent(outputPanel);

        splitPane2.setDividerLocation(250);
        splitPane1.setDividerLocation(250);
    }
    
    public Ops getOps() {
        return ops;
    }
    
    public void highlightOutput(Stage stage) {
        outputPanel.highlightOutput(stage);
    }
    
    public void generateOutput() {
        // figure out which input panel is selected and grab the
        // file from it.
        InputPanel inputPanel = directoryInputPanel;
        File inputFile = inputPanel.getInputFile();
        if (inputFile == null) {
            outputPanel.setOutputFiles(null);
            return;
        }
        Map<Stage, File> outputFiles = fireSight.generateOutputImages(
                pipelinePanel.getPipeline(), 
                inputPanel.getInputFile(), 
                workingDirectory);
        outputPanel.setOutputFiles(outputFiles);
    }
    
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Main main = new Main();
                main.setSize(1024, 768);
                main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                main.setVisible(true);
            }
        });
    }
}
